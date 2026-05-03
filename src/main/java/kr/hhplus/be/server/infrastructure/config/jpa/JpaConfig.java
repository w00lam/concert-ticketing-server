package kr.hhplus.be.server.infrastructure.config.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {
        "kr.hhplus.be.server.reservation.infrastructure.persistence",
        "kr.hhplus.be.server.payment.infrastructure.persistence",
        "kr.hhplus.be.server.point.infrastructure.persistence",
        "kr.hhplus.be.server.concert.infrastructure.persistence",
        "kr.hhplus.be.server.user.infrastructure.persistence"
})
/**
 * Provides Spring beans and infrastructure settings for infrastructure concerns.
 */
public class JpaConfig {

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
}
