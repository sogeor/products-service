package com.sogeor.service.products.service;

import com.sogeor.service.products.dto.ProductCategory;
import com.sogeor.service.products.dto.event.EventType;
import com.sogeor.service.products.dto.event.ProductCategoryEvent;
import com.sogeor.service.products.dto.web.ProductCategoryRequest;
import com.sogeor.service.products.dto.web.ProductCategoryResponse;
import com.sogeor.service.products.event.EventProducer;
import com.sogeor.service.products.mapper.ProductCategoryMapper;
import com.sogeor.service.products.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Limit;
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
public class ProductCategoryService {

    /**
     * @since 1.0.0-RC1
     */
    private final ProductCategoryRepository repository;

    /**
     * @since 1.0.0-RC1
     */
    private final ProductCategoryMapper mapper;

    /**
     * @since 1.0.0-RC1
     */
    private final EventProducer eventProducer;

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull ProductCategoryResponse> create(ProductCategoryRequest request) {
        final var category = mapper.toEntity(request);
        return repository.save(category)
                         .flatMap(saved -> eventProducer.send(ProductCategoryEvent.builder()
                                                                                  .type(EventType.PRODUCT_CATEGORY_CREATED)
                                                                                  .instant(Instant.now())
                                                                                  .category(saved)
                                                                                  .build())
                                                        .thenReturn(mapper.toResponse(saved)));
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull ProductCategoryResponse> get(UUID uuid) {
        return repository.findById(uuid).map(mapper::toResponse);
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull ProductCategoryResponse> createOrUpdate(UUID uuid, ProductCategoryRequest request) {
        final var category = mapper.toEntity(request);
        category.setUuid(uuid);
        return repository.save(category)
                         .flatMap(saved -> eventProducer.send(ProductCategoryEvent.builder()
                                                                                  .type(EventType.PRODUCT_CATEGORY_CREATED_OR_UPDATED)
                                                                                  .instant(Instant.now())
                                                                                  .category(saved)
                                                                                  .build())
                                                        .thenReturn(mapper.toResponse(saved)));
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Mono<@NotNull Void> delete(UUID uuid) {
        return repository.deleteById(uuid)
                         .then(Mono.defer(() -> eventProducer.send(ProductCategoryEvent.builder()
                                                                                       .type(EventType.PRODUCT_CATEGORY_DELETED)
                                                                                       .instant(Instant.now())
                                                                                       .category(
                                                                                               ProductCategory.builder()
                                                                                                              .uuid(uuid)
                                                                                                              .build())
                                                                                       .build()).then()));
    }

    /**
     * @since 1.0.0-RC1
     */
    @Transactional
    public Flux<@NotNull ProductCategoryResponse> search(String name, int limit) {
        return repository.findProductCategoriesByNameContainsIgnoreCase(name, Limit.of(limit)).map(mapper::toResponse);
    }

}
