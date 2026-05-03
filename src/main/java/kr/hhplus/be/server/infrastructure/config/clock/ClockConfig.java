package kr.hhplus.be.server.infrastructure.config.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
/**
 * Provides Spring beans and infrastructure settings for infrastructure concerns.
 */

@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone(); // 시스템 기본 시간대 사용
    }
}
