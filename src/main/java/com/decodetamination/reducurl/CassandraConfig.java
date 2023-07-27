package com.decodetamination.reducurl;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import org.cognitor.cassandra.migration.spring.CassandraMigrationAutoConfiguration;
import org.cognitor.cassandra.migration.spring.CassandraMigrationConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cassandra.DriverConfigLoaderBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class CassandraConfig {

    @Bean
    public DriverConfigLoaderBuilderCustomizer migrationProfileCreator(CassandraMigrationConfigurationProperties properties) {
        return builder -> builder.startProfile(properties.getExecutionProfileName())
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(60))
                .endProfile();
    }

    @Bean
    @Qualifier(CassandraMigrationAutoConfiguration.CQL_SESSION_BEAN_NAME)
    public CqlSession cassandraMigrationCqlSession(CqlSessionBuilder builder) {
        return builder.withKeyspace((String) null).build();
    }

    @Bean
    @Primary
    @DependsOn("migrationTask")
    public CqlSession cqlSession(CqlSessionBuilder builder) {
        return builder.build();
    }
}
