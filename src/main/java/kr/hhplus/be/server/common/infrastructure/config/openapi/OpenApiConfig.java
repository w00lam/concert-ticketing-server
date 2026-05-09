package kr.hhplus.be.server.common.infrastructure.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Provides Spring beans and infrastructure settings for infrastructure concerns.
 */

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI concertReservationOpenApi() {
        // Centralize public API metadata so Swagger UI presents the service consistently.
        return new OpenAPI()
                .info(new Info()
                        .title("Concert Reservation Platform API")
                        .description("대용량 트래픽 환경을 가정한 콘서트 티켓팅 서버 API 문서입니다.")
                        .version("1.0.0")
                        .license(new License().name("Portfolio Project")));
    }
}
