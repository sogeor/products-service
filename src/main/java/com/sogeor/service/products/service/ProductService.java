package com.sogeor.service.products.service;

import com.sogeor.service.products.dto.Product;
import com.sogeor.service.products.dto.event.EventType;
import com.sogeor.service.products.dto.event.ProductEvent;
import com.sogeor.service.products.dto.web.ProductRequest;
import com.sogeor.service.products.dto.web.ProductResponse;
import com.sogeor.service.products.event.EventProducer;
import com.sogeor.service.products.mapper.ProductMapper;
import com.sogeor.service.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * @since 1.0.0-RC1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    /**
     * @since 1.0.0-RC1
     */
    private final ProductRepository repository;

    /**
     * @since 1.0.0-RC1
     */
    private final ProductMapper mapper;

    /**
     * @since 1.0.0-RC1
     */
    private final EventProducer eventProducer;

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull ProductResponse> create(ProductRequest request) {
        final var product = mapper.toEntity(request);
        product.setUuid(UUID.randomUUID());
        return repository.save(product)
                         .flatMap(saved -> eventProducer.send(ProductEvent.builder()
                                                                          .type(EventType.PRODUCT_CREATED)
                                                                          .instant(Instant.now())
                                                                          .product(saved)
                                                                          .build())
                                                        .thenReturn(mapper.toResponse(saved)));
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull ProductResponse> get(UUID uuid) {
        return repository.findById(uuid).map(mapper::toResponse);
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Flux<@NotNull ProductResponse> get(int page, int count) {
        return repository.findAll().skip((long) page * count).take(count).map(mapper::toResponse);
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Flux<@NotNull ProductResponse> get(UUID category, int page, int count) {
        return repository.findProductsByCategory(category, Pageable.ofSize(count).withPage(page))
                         .map(mapper::toResponse);
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull ProductResponse> createOrUpdate(UUID uuid, ProductRequest request) {
        final var product = mapper.toEntity(request);
        product.setUuid(uuid);
        return repository.save(product)
                         .flatMap(saved -> eventProducer.send(ProductEvent.builder()
                                                                          .type(EventType.PRODUCT_CREATED)
                                                                          .instant(Instant.now())
                                                                          .product(saved)
                                                                          .build())
                                                        .thenReturn(mapper.toResponse(saved)));
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull Void> delete(UUID uuid) {
        return repository.deleteById(uuid)
                         .then(Mono.defer(() -> eventProducer.send(ProductEvent.builder()
                                                                               .type(EventType.PRODUCT_DELETED)
                                                                               .instant(Instant.now())
                                                                               .product(Product.builder()
                                                                                               .uuid(uuid)
                                                                                               .build())
                                                                               .build()).then()));
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Flux<@NotNull ProductResponse> search(String name, int limit) {
        return repository.findProductsByNameContainsIgnoreCase(name, Limit.of(limit)).map(mapper::toResponse);
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull Long> about() {
        return repository.count();
    }

}
