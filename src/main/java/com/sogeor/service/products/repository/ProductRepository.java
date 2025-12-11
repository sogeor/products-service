package com.sogeor.service.products.repository;

import com.sogeor.service.products.domain.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

    Flux<Product> findByNameContainingIgnoreCase(String name);

    Flux<Product> findByCategoryIgnoreCase(String category);

}
