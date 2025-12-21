package com.sogeor.service.products.controller;

import com.sogeor.service.products.dto.web.ProductRequest;
import com.sogeor.service.products.dto.web.ProductResponse;
import com.sogeor.service.products.service.ProductService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @since 1.0.0-RC1
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    /**
     * @since 1.0.0-RC1
     */
    private final ProductService service;

    /**
     * @since 1.0.0-RC1
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<@NotNull ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return service.create(request);
    }

    /**
     * @since 1.0.0-RC1
     */
    @GetMapping("/{uuid}")
    public Mono<@NotNull ResponseEntity<@NotNull ProductResponse>> get(@PathVariable @NonNull UUID uuid) {
        return service.get(uuid).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * @since 1.0.0-RC1
     */
    @GetMapping("/")
    public Flux<@NotNull ProductResponse> get(@RequestParam(required = false) UUID category, @RequestParam int page,
                                              @RequestParam int count) {
        return category == null ? service.get(page, count) : service.get(category, page, count);
    }

    /**
     * @since 1.0.0-RC1
     */
    @PutMapping("/{uuid}")
    public Mono<@NotNull ResponseEntity<@NotNull ProductResponse>> createOrUpdate(@PathVariable @NonNull UUID uuid,
                                                                                  @Valid @RequestBody ProductRequest request) {
        return service.createOrUpdate(uuid, request)
                      .map(ResponseEntity::ok)
                      .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * @since 1.0.0-RC1
     */
    @DeleteMapping("/{uuid}")
    public Mono<@NotNull ResponseEntity<@NotNull Void>> delete(@PathVariable @NonNull UUID uuid) {
        return service.delete(uuid)
                      .<@NotNull ResponseEntity<@NotNull Void>>then(
                              Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build()))
                      .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * @since 1.0.0-RC1
     */
    @GetMapping("/search")
    public Flux<@NotNull ProductResponse> search(@RequestParam @NonNull String name,
                                                 @RequestParam(defaultValue = "16") int limit) {
        return service.search(name, limit);
    }

    /**
     * @since 1.0.0-RC1
     */
    @GetMapping("/about")
    public Mono<@NotNull Long> about() {
        return service.about();
    }

}
