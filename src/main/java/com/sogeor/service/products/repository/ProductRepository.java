package com.sogeor.service.products.repository;

import com.sogeor.service.products.domain.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<@NotNull Product, @NotNull String> {

    Flux<@NotNull Product> findByNameContainingIgnoreCase(String name);

    Flux<@NotNull Product> findByCategoryIgnoreCase(String category);

}
