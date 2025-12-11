package com.sogeor.service.products.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogeor.service.products.dto.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private static final String TOPIC = "product-updates";

    private final KafkaTemplate<@NotNull String, @NotNull String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public Mono<@NotNull Void> sendEvent(ProductEvent event) {
        return Mono.fromCallable(() -> {
                       try {
                           return objectMapper.writeValueAsString(event);
                       } catch (JsonProcessingException e) {
                           throw new RuntimeException("Error converting event to JSON", e);
                       }
                   })
                   .flatMap(json -> Mono.fromFuture(kafkaTemplate.send(TOPIC, event.getProductId(), json)))
                   .doOnSuccess(result -> log.info("Sent event: {} to topic: {}", event.getEventType(), TOPIC))
                   .doOnError(e -> log.error("Error sending event: {}", e.getMessage()))
                   .then();
    }

}
