package com.sogeor.service.products.dto.event;

import com.sogeor.service.products.dto.ProductCategory;
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
public class ProductCategoryEvent {

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
    private @NonNull ProductCategory category;

}
