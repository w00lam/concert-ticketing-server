package kr.hhplus.be.server.unit.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

class ActuatorConfigurationTest {

    @Test
    void healthEndpointExposesOnlyOperationalStatus() throws IOException {
        PropertySource<?> properties = loadApplicationProperties();

        assertThat(properties.getProperty("management.endpoints.web.exposure.include"))
                .isEqualTo("health,info");
        assertThat(properties.getProperty("management.endpoint.health.show-details"))
                .isEqualTo("never");
        assertThat(properties.getProperty("management.endpoint.health.probes.enabled"))
                .isEqualTo(true);
    }

    @Test
    void readinessIncludesExternalDependencies() throws IOException {
        PropertySource<?> properties = loadApplicationProperties();

        assertThat(properties.getProperty("management.endpoint.health.group.liveness.include"))
                .isEqualTo("livenessState");
        assertThat(properties.getProperty("management.endpoint.health.group.readiness.include"))
                .isEqualTo("readinessState,db,redis");
    }

    private PropertySource<?> loadApplicationProperties() throws IOException {
        // Load the YAML directly so the test stays fast and does not require database or Redis.
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load("application", new ClassPathResource("application.yml"));

        return sources.get(0);
    }
}
