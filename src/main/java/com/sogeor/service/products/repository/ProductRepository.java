package com.sogeor.service.products.repository;

import com.sogeor.service.products.dto.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * @since 1.0.0-RC1
 */
@Repository
public interface ProductRepository extends ReactiveMongoRepository<@NotNull Product, @NotNull String> {

    /**
     * @since 1.0.0-RC1
     */
    Flux<@NotNull Product> findProductsByCategory(String category, Pageable pageable);

    /**
     * @since 1.0.0-RC1
     */
    Flux<@NotNull Product> findProductsByNameContainsIgnoreCase(String name, Limit limit);

}
