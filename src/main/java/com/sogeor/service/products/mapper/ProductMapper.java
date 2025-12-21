package com.sogeor.service.products.mapper;

import com.sogeor.service.products.dto.Product;
import com.sogeor.service.products.dto.web.ProductRequest;
import com.sogeor.service.products.dto.web.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @since 1.0.0-RC1
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * @since 1.0.0-RC1
     */
    @Mappings({@Mapping(target = "_id", ignore = true), @Mapping(target = "uuid", ignore = true)})
    Product toEntity(ProductRequest request);

    /**
     * @since 1.0.0-RC1
     */
    ProductResponse toResponse(Product product);

}
