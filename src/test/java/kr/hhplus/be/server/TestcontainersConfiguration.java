package kr.hhplus.be.server;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class TestcontainersConfiguration {
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("hhplus")
            .withUsername("test")
            .withPassword("test");
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(6379);

    private TestcontainersConfiguration() {
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();

            // Register container ports before Spring creates the DataSource and Redis connection factory.
            TestPropertyValues.of(
                    "spring.datasource.url=" + MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC",
                    "spring.datasource.username=" + MYSQL_CONTAINER.getUsername(),
                    "spring.datasource.password=" + MYSQL_CONTAINER.getPassword(),
                    "spring.data.redis.host=" + REDIS_CONTAINER.getHost(),
                    "spring.data.redis.port=" + REDIS_CONTAINER.getMappedPort(6379),
                    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect"
            ).applyTo(applicationContext.getEnvironment());
        }
    }

    private static synchronized void startContainers() {
        if (!MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.start();
        }
        if (!REDIS_CONTAINER.isRunning()) {
            REDIS_CONTAINER.start();
        }
    }
}
