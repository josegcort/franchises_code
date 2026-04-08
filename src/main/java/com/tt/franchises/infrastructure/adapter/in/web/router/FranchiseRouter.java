package com.tt.franchises.infrastructure.adapter.in.web.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.tt.franchises.infrastructure.adapter.in.web.handler.FranchiseHandler;

/**
 * Router configuration for franchise-related HTTP endpoints.
 */
@Configuration
public class FranchiseRouter {

	// Define the router function for handling franchise-related requests
	@Bean
	public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
		return RouterFunctions.route()//
				.POST("/franchises", handler::create)//
				.GET("/franchises/{id}", handler::getById)//
				.GET("/franchises", handler::getAll)//
				.build();
	}
}