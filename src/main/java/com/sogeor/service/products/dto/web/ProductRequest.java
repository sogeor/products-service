package com.sogeor.service.products.dto.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @since 1.0.0-RC1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    /**
     * @since 1.0.0-RC1
     */
    private @NotNull UUID category;

    /**
     * @since 1.0.0-RC1
     */
    @NotBlank
    private String name;

    /**
     * @since 1.0.0-RC1
     */
    private String description;

    /**
     * @since 1.0.0-RC1
     */
    @Min(1)
    private @NotNull BigDecimal price;

}
