package kr.hhplus.be.server.common.infrastructure.config.kafka;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Configures bounded retry and dead-letter recovery for record listeners.
 */
@Configuration
public class KafkaErrorHandlingConfig {
    static final long RETRY_INTERVAL_MILLIS = 1_000L;
    static final long MAX_RETRIES = 2L;
    private static final String DLT_SUFFIX = "-dlt";

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(record.topic() + DLT_SUFFIX, record.partition())
        );

        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(RETRY_INTERVAL_MILLIS, MAX_RETRIES)
        );
    }
}
