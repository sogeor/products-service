package com.sogeor.service.products.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0.0-RC1
 */
@RequiredArgsConstructor
public enum EventTopic {

    /**
     * @since 1.0.0-RC1
     */
    CATEGORY("products-service.category"),

    /**
     * @since 1.0.0-RC1
     */
    PRODUCT("products-service.product");

    /**
     * @since 1.0.0-RC1
     */
    @Getter
    private final @NotNull String literal;
}
