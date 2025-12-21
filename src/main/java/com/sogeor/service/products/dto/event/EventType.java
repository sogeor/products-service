package com.sogeor.service.products.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0.0-RC1
 */
@RequiredArgsConstructor
public enum EventType {

    /**
     * @since 1.0.0-RC1
     */
    PRODUCT_CATEGORY_CREATED(EventTopic.CATEGORY),

    /**
     * @since 1.0.0-RC1
     */
    PRODUCT_CATEGORY_CREATED_OR_UPDATED(EventTopic.CATEGORY),

    /**
     * @since 1.0.0-RC1
     */
    PRODUCT_CATEGORY_DELETED(EventTopic.CATEGORY),

    /**
     * @since 1.0.0-RC1
     */
    PRODUCT_CREATED(EventTopic.PRODUCT),

    /**
     * @since 1.0.0-RC1
     */
    PRODUCT_CREATED_OR_UPDATED(EventTopic.PRODUCT),

    /**
     * @since 1.0.0-RC1
     */
    PRODUCT_DELETED(EventTopic.PRODUCT);

    /**
     * @since 1.0.0-RC1
     */
    @Getter
    private final @NotNull EventTopic topic;
}
