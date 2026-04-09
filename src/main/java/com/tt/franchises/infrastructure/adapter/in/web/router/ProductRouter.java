package com.tt.franchises.infrastructure.adapter.in.web.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.tt.franchises.infrastructure.adapter.in.web.handler.ProductHandler;

/**
 * Router configuration for product-related HTTP endpoints.
 */
@Configuration
public class ProductRouter {

	// Define the router function for handling product-related requests
	@Bean
	public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
		return RouterFunctions.route()//
				.POST("/products", handler::create)//
				.PATCH("/products/{id}", handler::updateStock)//
				.DELETE("/products/{id}", handler::delete)//
				.GET("/products/{id}", handler::getById)//
				.GET("/branches/{branchId}/products", handler::getByBranchId)//
				.GET("/products", handler::getAll)//
				.GET("/franchises/{franchiseId}/top-stock", handler::getTopStockPerBranch)//
				.build();
	}
}