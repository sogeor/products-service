package com.sogeor.service.products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.UUID;

/**
 * @since 1.0.0-RC1
 */
@Document(collection = "product_categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {

    /**
     * @since 1.0.0-RC1
     */
    @Id
    @Field(targetType = FieldType.STRING)
    private UUID uuid;

    /**
     * @since 1.0.0-RC1
     */
    private String name;

    /**
     * @since 1.0.0-RC1
     */
    @Field(write = Field.Write.ALWAYS)
    private String description;

}
