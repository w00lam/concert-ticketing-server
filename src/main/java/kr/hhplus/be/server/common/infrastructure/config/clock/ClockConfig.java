package kr.hhplus.be.server.common.infrastructure.config.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone(); // 시스템 기본 시간대 사용
    }
}
