package kr.hhplus.be.server.integration.tokenqueue;

import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenQueueMultiInstanceIntegrationTest extends ReservationIntegrationTestBase {
    private static final String QUEUE_KEY = "queue:token";
    private static final int QUEUE_SIZE = 1_000;
    private static final int APPLICATION_INSTANCE_COUNT = 2;
    private static final int DEQUEUE_PER_INSTANCE = QUEUE_SIZE / APPLICATION_INSTANCE_COUNT;

    @Autowired
    private TokenQueueUseCase tokenQueueUseCase;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @BeforeEach
    void clearQueue() {
        redisTemplate.delete(QUEUE_KEY);
    }

    @Test
    @DisplayName("독립 JVM 두 개가 같은 Redis 대기열을 dequeue해도 사용자가 중복 입장하지 않는다")
    void independentApplicationInstancesDequeueEachUserOnce() throws Exception {
        List<String> userIds = IntStream.range(0, QUEUE_SIZE)
                .mapToObj(index -> "multi-instance-user-" + index + "-" + UUID.randomUUID())
                .toList();
        userIds.forEach(tokenQueueUseCase::enqueueUser);

        assertThat(tokenQueueUseCase.getQueueLength()).isEqualTo(QUEUE_SIZE);

        Path workDirectory = Files.createTempDirectory("token-queue-multi-instance-");
        Path startSignal = workDirectory.resolve("start.signal");
        Path classpathArguments = createClasspathArguments(workDirectory);
        List<WorkerHandle> workers = new ArrayList<>();
        try {
            for (int instanceIndex = 0; instanceIndex < APPLICATION_INSTANCE_COUNT; instanceIndex++) {
                workers.add(startWorker(workDirectory, startSignal, classpathArguments, instanceIndex));
            }

            Awaitility.await()
                    .atMost(Duration.ofSeconds(10))
                    .until(() -> workers.stream().allMatch(worker -> Files.exists(worker.readySignal())));
            Files.writeString(startSignal, "start", StandardCharsets.UTF_8);

            for (WorkerHandle worker : workers) {
                waitForWorker(worker);
            }

            List<String> poppedUsers = workers.stream()
                    .flatMap(worker -> readLines(worker.output()).stream())
                    .toList();

            assertThat(poppedUsers).hasSize(QUEUE_SIZE);
            assertThat(new HashSet<>(poppedUsers)).hasSize(QUEUE_SIZE);
            assertThat(poppedUsers).containsExactlyInAnyOrderElementsOf(userIds);
            assertThat(tokenQueueUseCase.getQueueLength()).isZero();

            System.out.printf(
                    Locale.ROOT,
                    "queue_multi_instance_users=%d, application_instances=%d, dequeue_per_instance=%d, dequeue_duplicates=%d, queue_length_after_dequeue=%d%n",
                    QUEUE_SIZE,
                    APPLICATION_INSTANCE_COUNT,
                    DEQUEUE_PER_INSTANCE,
                    poppedUsers.size() - new HashSet<>(poppedUsers).size(),
                    tokenQueueUseCase.getQueueLength()
            );
        } finally {
            workers.forEach(this::stopWorkerIfRunning);
            deleteWorkerFiles(workDirectory, startSignal, classpathArguments, workers);
        }
    }

    private Path createClasspathArguments(Path workDirectory) throws IOException {
        Path classpathArguments = workDirectory.resolve("worker-classpath.args");
        Files.writeString(
                classpathArguments,
                "-cp\n\"" + System.getProperty("java.class.path") + "\"\n",
                StandardCharsets.UTF_8
        );
        return classpathArguments;
    }

    private WorkerHandle startWorker(
            Path workDirectory,
            Path startSignal,
            Path classpathArguments,
            int instanceIndex
    ) throws IOException {
        Path readySignal = workDirectory.resolve("instance-" + instanceIndex + ".ready");
        Path output = workDirectory.resolve("instance-" + instanceIndex + ".users");
        String javaExecutable = Path.of(
                System.getProperty("java.home"),
                "bin",
                System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win") ? "java.exe" : "java"
        ).toString();

        Process process = new ProcessBuilder(
                javaExecutable,
                "@" + classpathArguments.toAbsolutePath(),
                TokenQueueDequeueWorker.class.getName(),
                redisHost,
                String.valueOf(redisPort),
                String.valueOf(DEQUEUE_PER_INSTANCE),
                startSignal.toString(),
                readySignal.toString(),
                output.toString()
        ).redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();

        return new WorkerHandle(process, readySignal, output);
    }

    private void waitForWorker(WorkerHandle worker) throws Exception {
        boolean finished = worker.process().waitFor(30, TimeUnit.SECONDS);
        assertThat(finished)
                .as("dequeue worker did not finish")
                .isTrue();
        assertThat(worker.process().exitValue())
                .as("dequeue worker failed")
                .isZero();
        assertThat(Files.exists(worker.output())).isTrue();
    }

    private List<String> readLines(Path output) {
        try {
            return Files.readAllLines(output, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read worker output: " + output, exception);
        }
    }

    private void stopWorkerIfRunning(WorkerHandle worker) {
        if (worker.process().isAlive()) {
            worker.process().destroyForcibly();
        }
    }

    private void deleteWorkerFiles(
            Path workDirectory,
            Path startSignal,
            Path classpathArguments,
            List<WorkerHandle> workers
    ) {
        try {
            Files.deleteIfExists(startSignal);
            Files.deleteIfExists(classpathArguments);
            for (WorkerHandle worker : workers) {
                Files.deleteIfExists(worker.readySignal());
                Files.deleteIfExists(worker.output());
            }
            Files.deleteIfExists(workDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to clean up worker files", exception);
        }
    }

    private record WorkerHandle(Process process, Path readySignal, Path output) {
    }
}
