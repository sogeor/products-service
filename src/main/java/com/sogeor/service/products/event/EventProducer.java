package com.sogeor.service.products.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogeor.service.products.dto.event.EventType;
import com.sogeor.service.products.dto.event.ProductCategoryEvent;
import com.sogeor.service.products.dto.event.ProductEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @since 1.0.0-RC1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducer {

    /**
     * @since 1.0.0-RC1
     */
    private final @NotNull KafkaTemplate<@NotNull String, @NotNull String> kafkaTemplate;

    /**
     * @since 1.0.0-RC1
     */
    private final @NotNull ObjectMapper objectMapper;

    /**
     * @since 1.0.0-RC1
     */
    private @NotNull Mono<@NotNull Void> send(final @NotNull EventType type, final @NotNull String key,
                                              final @NotNull Object data) {
        return Mono.fromCallable(() -> {
                       try {
                           return objectMapper.writeValueAsString(data);
                       } catch (final @NotNull JsonProcessingException exception) {
                           throw new RuntimeException(exception);
                       }
                   })
                   .flatMap(json -> Mono.fromFuture(kafkaTemplate.send(type.getTopic().getLiteral(), key, json)))
                   .doOnSuccess(ignored -> log.info("Sending an event (topic = {}; type = {}; key = {})...",
                                                    type.getTopic().getLiteral(), type, key))
                   .doOnError(throwable -> log.error("Failed to send an event (topic = {}; type = {}; key = {}): {}",
                                                     type.getTopic().getLiteral(), type, key, throwable.getMessage()))
                   .then();
    }

    /**
     * @since 1.0.0-RC1
     */
    public @NotNull Mono<@NotNull Void> send(final @NonNull ProductCategoryEvent event) {
        return send(event.getType(), event.getCategory().getUuid(), event);
    }

    /**
     * @since 1.0.0-RC1
     */
    public @NotNull Mono<@NotNull Void> send(final @NonNull ProductEvent event) {
        return send(event.getType(), event.getProduct().getUuid(), event);
    }

}
