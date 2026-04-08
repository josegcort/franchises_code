package com.tt.franchises.infrastructure.adapter.in.web;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.application.dto.FranchiseRequest;
import com.tt.franchises.application.usecase.FranchiseUseCase;
import com.tt.franchises.domain.model.Franchise;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class FranchiseHandler {

	private final FranchiseUseCase useCase;
	private final Validator validator;

	public Mono<ServerResponse> create(ServerRequest request) {
		return request.bodyToMono(FranchiseRequest.class).flatMap(req -> {

			Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(req);

			if (!violations.isEmpty()) {
				String error = violations.iterator().next().getMessage();

				return Mono.error(//
						new ResponseStatusException(//
								HttpStatus.BAD_REQUEST, //
								error//
				));//
			}

			return useCase.create(new Franchise(null, req.getName(), List.of()));
		}).flatMap(franchise -> ServerResponse//
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
