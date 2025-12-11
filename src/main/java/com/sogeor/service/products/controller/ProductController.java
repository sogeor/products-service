package com.sogeor.service.products.controller;

import com.sogeor.service.products.dto.ProductRequest;
import com.sogeor.service.products.dto.ProductResponse;
import com.sogeor.service.products.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
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

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Flux<@NotNull ProductResponse> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProducts(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Mono<@NotNull ResponseEntity<@NotNull ProductResponse>> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                             .map(ResponseEntity::ok)
                             .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Flux<@NotNull ProductResponse> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<@NotNull ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}/update")
    public Mono<@NotNull ResponseEntity<@NotNull ProductResponse>> updateProduct(@PathVariable String id,
                                                                                 @RequestBody @Valid ProductRequest request) {
        return productService.updateProduct(id, request)
                             .map(ResponseEntity::ok)
                             .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/delete")
    public Mono<@NotNull ResponseEntity<@NotNull Void>> deleteProduct(@PathVariable String id) {
        return productService.getProductById(id)
                             .flatMap(p -> productService.deleteProduct(id)
                                                         .then(Mono.just(new ResponseEntity<@NotNull Void>(
                                                                 HttpStatus.NO_CONTENT))))
                             .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
