package com.sogeor.service.products.dto.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @since 1.0.0-RC1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponse {

    /**
     * @since 1.0.0-RC1
     */
    private @NonNull String uuid;

    /**
     * @since 1.0.0-RC1
     */
    private @NonNull String name;

    /**
     * @since 1.0.0-RC1
     */
    private String description;

}
