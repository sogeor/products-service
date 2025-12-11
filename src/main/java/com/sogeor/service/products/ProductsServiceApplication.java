package com.sogeor.service.products;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @since 1.0.0-RC1
 */
@OpenAPIDefinition(servers = @Server(url = "https://api.sogeor.com"))
@SpringBootApplication
public class ProductsServiceApplication {

    /**
     * @since 1.0.0-RC1
     */
    static void main(final @NotNull String... arguments) {
        SpringApplication.run(ProductsServiceApplication.class, arguments);
    }

}
