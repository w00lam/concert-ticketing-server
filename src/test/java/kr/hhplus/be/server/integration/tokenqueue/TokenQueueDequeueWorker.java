package kr.hhplus.be.server.integration.tokenqueue;

import kr.hhplus.be.server.tokenqueue.infrastructure.persistence.TokenQueueRepositoryImpl;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the dequeue portion of the integration test in an independent JVM.
 */
public final class TokenQueueDequeueWorker {
    private TokenQueueDequeueWorker() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            throw new IllegalArgumentException("Expected host, port, count, signal, ready, and output paths");
        }

        String redisHost = args[0];
        int redisPort = Integer.parseInt(args[1]);
        int dequeueCount = Integer.parseInt(args[2]);
        Path startSignal = Path.of(args[3]);
        Path readySignal = Path.of(args[4]);
        Path output = Path.of(args[5]);

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisHost, redisPort);
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        try {
            connectionFactory.afterPropertiesSet();
            redisTemplate.afterPropertiesSet();

            Files.writeString(readySignal, "ready", StandardCharsets.UTF_8);
            while (!Files.exists(startSignal)) {
                Thread.sleep(5);
            }

            TokenQueueRepositoryImpl repository = new TokenQueueRepositoryImpl(redisTemplate);
            List<String> poppedUsers = new ArrayList<>(dequeueCount);
            for (int index = 0; index < dequeueCount; index++) {
                String poppedUser = repository.popNextUser();
                if (poppedUser == null) {
                    throw new IllegalStateException("Expected a queued user at dequeue index " + index);
                }
                poppedUsers.add(poppedUser);
            }

            Files.write(output, poppedUsers, StandardCharsets.UTF_8);
        } finally {
            connectionFactory.destroy();
        }
    }
}
