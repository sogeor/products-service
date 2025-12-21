package com.sogeor.service.products.dto.event;

import com.sogeor.service.products.dto.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

/**
 * @since 1.0.0-RC1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {

    /**
     * @since 1.0.0-RC1
     */
    private @NonNull EventType type;

    /**
     * @since 1.0.0-RC1
     */
    private @NonNull Instant instant;

    /**
     * @since 1.0.0-RC1
     */
    private @NonNull Product product;

}
