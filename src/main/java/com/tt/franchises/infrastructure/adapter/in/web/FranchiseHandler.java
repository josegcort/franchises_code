package com.tt.franchises.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.tt.franchises.application.dto.FranchiseRequest;
import com.tt.franchises.application.usecase.FranchiseUseCase;
import com.tt.franchises.domain.model.Franchise;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class FranchiseHandler {

	private final FranchiseUseCase useCase;

	public Mono<ServerResponse> create(ServerRequest request) {
		return request.bodyToMono(FranchiseRequest.class)//
				.map(req -> new Franchise(null, req.getName(), List.of()))//
				.flatMap(useCase::create)//
				.flatMap(franchise -> ServerResponse//
						.status(HttpStatus.CREATED)//
						.bodyValue(franchise));
	}

	public Mono<ServerResponse> getById(ServerRequest request) {
		String id = request.pathVariable("id");
		return useCase.getById(id)//
				.flatMap(f -> ServerResponse.ok().bodyValue(f));
	}

	public Mono<ServerResponse> getAll(ServerRequest request) {
		return ServerResponse.ok().body(useCase.getAll(), Franchise.class);
	}

}
