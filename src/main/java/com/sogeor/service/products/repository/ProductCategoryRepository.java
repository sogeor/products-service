package com.sogeor.service.products.repository;

import com.sogeor.service.products.dto.ProductCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * @since 1.0.0-RC1
 */
@Repository
public interface ProductCategoryRepository extends ReactiveMongoRepository<@NotNull ProductCategory, @NotNull String> {

    /**
     * @since 1.0.0-RC1
     */
    Flux<@NotNull ProductCategory> findProductCategoriesByNameContainsIgnoreCase(String name, Limit limit);

}
