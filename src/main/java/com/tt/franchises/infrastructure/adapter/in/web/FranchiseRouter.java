package com.tt.franchises.infrastructure.adapter.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FranchiseRouter {

	@Bean
	public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
		return RouterFunctions.route()//
				.POST("/franchises", handler::create)//
				.GET("/franchises/{id}", handler::getById)//
				.GET("/franchises", handler::getAll)//
				.build();
	}
}