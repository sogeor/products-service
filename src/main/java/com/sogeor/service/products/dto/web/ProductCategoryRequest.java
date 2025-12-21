package com.sogeor.service.products.dto.web;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 1.0.0-RC1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryRequest {

    /**
     * @since 1.0.0-RC1
     */
    @NotBlank
    private String name;

    /**
     * @since 1.0.0-RC1
     */
    private String description;

}
