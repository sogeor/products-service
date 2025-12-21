package com.sogeor.service.products.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.Arrays;
import java.util.UUID;

/**
 * @since 1.0.0-RC1
 */
@Configuration
@EnableReactiveMongoRepositories
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(new UUIDToStringConverter(), new StringToUUIDConverter()));
    }

    public static class UUIDToStringConverter implements Converter<@NotNull UUID, @NotNull String> {

        @Override
        public String convert(UUID source) {
            return source.toString();
        }

    }

    public static class StringToUUIDConverter implements Converter<@NotNull String, @NotNull UUID> {

        @Override
        public UUID convert(String source) {
            return UUID.fromString(source);
        }

    }

}
