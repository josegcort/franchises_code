package com.tt.franchises.infrastructure.adapter.in.web.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.tt.franchises.infrastructure.adapter.in.web.handler.BranchHandler;

/**
 * Router configuration for branch-related HTTP endpoints.
 */
@Configuration
public class BranchRouter {

	// Define the router function for handling branch-related requests
	@Bean
	public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
		return RouterFunctions.route()//
				.POST("/branches", handler::create)//
				.GET("/branches/{id}", handler::getById)//
				.GET("/franchises/{franchiseId}/branches", handler::getByFranchiseId)//
				.GET("/branches", handler::getAll)//
				.build();
	}
}