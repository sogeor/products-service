package com.sogeor.service.products.mapper;

import com.sogeor.service.products.dto.ProductCategory;
import com.sogeor.service.products.dto.web.ProductCategoryRequest;
import com.sogeor.service.products.dto.web.ProductCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @since 1.0.0-RC1
 */
@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    /**
     * @since 1.0.0-RC1
     */
    @Mappings({@Mapping(target = "_id", ignore = true), @Mapping(target = "uuid", ignore = true)})
    ProductCategory toEntity(ProductCategoryRequest request);

    /**
     * @since 1.0.0-RC1
     */
    ProductCategoryResponse toResponse(ProductCategory category);

}
