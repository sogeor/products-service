package com.sogeor.service.products.mapper;

import com.sogeor.service.products.domain.Product;
import com.sogeor.service.products.dto.ProductRequest;
import com.sogeor.service.products.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest request);

}
