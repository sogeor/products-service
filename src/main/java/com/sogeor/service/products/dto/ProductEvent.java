package com.sogeor.service.products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEvent {

    private String eventType;

    private String productId;

    private String name;

    private BigDecimal price;

    private String category;

    private Instant timestamp;

}
