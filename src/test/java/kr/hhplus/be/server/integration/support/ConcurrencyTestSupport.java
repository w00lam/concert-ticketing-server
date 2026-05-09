package kr.hhplus.be.server.integration.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ConcurrencyTestSupport {
    private ConcurrencyTestSupport() {
    }

    public static <T> Result<T> runConcurrently(int threadCount, ConcurrentTask<T> task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        List<T> successes = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());

        for (int index = 0; index < threadCount; index++) {
            int taskIndex = index;
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    successes.add(task.run(taskIndex));
                } catch (Throwable failure) {
                    failures.add(failure);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        return new Result<>(List.copyOf(successes), List.copyOf(failures));
    }

    @FunctionalInterface
    public interface ConcurrentTask<T> {
        T run(int index) throws Exception;
    }

    public record Result<T>(List<T> successes, List<Throwable> failures) {
        public long matchingSuccessCount(Predicate<? super T> predicate) {
            return successes.stream()
                    .filter(predicate)
                    .count();
        }

        public <R> List<R> flatMapSuccesses(Function<? super T, ? extends Stream<? extends R>> mapper) {
            return successes.stream()
                    .flatMap(mapper)
                    .toList();
        }
    }
}
