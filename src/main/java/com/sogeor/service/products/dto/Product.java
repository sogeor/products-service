package com.sogeor.service.products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * @since 1.0.0-RC1
 */
@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * @since 1.0.0-RC1
     */
    @Id
    private String uuid;

    /**
     * @since 1.0.0-RC1
     */
    private String category;

    /**
     * @since 1.0.0-RC1
     */
    private String name;

    /**
     * @since 1.0.0-RC1
     */
    @Field(write = Field.Write.ALWAYS)
    private String description;

    /**
     * @since 1.0.0-RC1
     */
    @Field(write = Field.Write.ALWAYS)
    private BigDecimal price;

}
