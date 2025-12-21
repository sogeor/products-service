package com.sogeor.service.products.config;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

/**
 * @since 1.0.0-RC1
 */
@Configuration
@EnableReactiveMongoRepositories("com.sogeor.service.products")
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(
                Arrays.asList(new UUIDToStringConverter(), new StringToUUIDConverter(), new ObjectIdToUUIDConverter()));
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
            try {
                return UUID.fromString(source);
            } catch (RuntimeException e) {
                return null;
            }
        }

    }

    public static class ObjectIdToUUIDConverter implements Converter<@NotNull ObjectId, @NotNull UUID> {

        private static final ZoneOffset UTC = ZoneOffset.UTC;

        private static final Instant UUID_1_EPOCH = LocalDateTime.of(1582, 10, 15, 0, 0, 0).toInstant(UTC);

        private static final long UUID_TICKS_PER_SECOND = 10_000_000L;

        private static long unixTimeToUuidTime(Instant instant) {
            Duration duration = Duration.between(UUID_1_EPOCH, instant);
            return duration.getSeconds() * UUID_TICKS_PER_SECOND + duration.getNano() / 100;
        }

        public static UUID objectIdToUuid(ObjectId objectId) {
            // Преобразуем ObjectId в массив байтов
            byte[] bytes = objectId.toByteArray();

            // Получаем время генерации ObjectId
            Instant generationTime = objectId.getDate().toInstant().atZone(UTC).toInstant();

            // Преобразуем время в UUID время
            long time = unixTimeToUuidTime(generationTime);

            // Корректируем время с использованием 4-го байта ObjectId
            time |= (bytes[4] >> 6) & 0x3L;

            // Формируем most significant bits
            long mostSigBits = 0x1000L | ((time >> 48) & 0x0FFFL) | ((time >> 16) & 0xFFFF0000L) | (time << 32);

            // Формируем least significant bits (версия 2 - DCE Security)
            long leastSigBits = 2L << 62 | ((bytes[4] & 0x3F) & 0xFFL) << 56 | ((bytes[5] & 0xFFL) << 48) |
                                ((bytes[6] & 0xFFL) << 40) | ((bytes[7] & 0xFFL) << 32) | ((bytes[8] & 0xFFL) << 24) |
                                ((bytes[9] & 0xFFL) << 16) | ((bytes[10] & 0xFFL) << 8) | (bytes[11] & 0xFFL);

            return new UUID(mostSigBits, leastSigBits);
        }

        @Override
        public UUID convert(ObjectId source) {
            try {
                return objectIdToUuid(source);
            } catch (RuntimeException e) {
                return null;
            }
        }

    }

}
