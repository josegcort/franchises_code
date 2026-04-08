package com.tt.franchises.infrastructure.adapter.in.web;

import java.util.List;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.application.dto.FranchiseRequest;
import com.tt.franchises.application.usecase.FranchiseUseCase;
import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.tools.Operations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Handler for managing franchise-related HTTP requests.
 */
@Slf4j
@Component
@AllArgsConstructor
public class FranchiseHandler {

	private final FranchiseUseCase useCase;
	private final Validator validator;
	private final MessageSource msgSrc;

	// Handler method for creating a new franchise
	public Mono<ServerResponse> create(ServerRequest request) {
		return request.bodyToMono(FranchiseRequest.class).flatMap(req -> {

			// Validate the request using the Validator
			Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(req);

			// If there are validation errors, return a 400 Bad Request response with the error message
			if (!violations.isEmpty()) {
				String error = violations.iterator().next().getMessage();
				String errorValue = Operations.getMessage(msgSrc, error);

				log.error(errorValue);
				
				return Mono.error(//
						new ResponseStatusException(//
								HttpStatus.BAD_REQUEST, //
								errorValue//
				));//
			}

			return useCase.create(new Franchise(null, req.getName(), List.of()));
		}).flatMap(franchise -> ServerResponse//
				.status(HttpStatus.CREATED)//
				.bodyValue(franchise));
	}

	// Handler method for returning a franchise by its ID
	public Mono<ServerResponse> getById(ServerRequest request) {
		String id = request.pathVariable("id");
		return useCase.getById(id)//
				.flatMap(f -> ServerResponse.ok().bodyValue(f));
	}

	// Handler method for returning all franchises
	public Mono<ServerResponse> getAll(ServerRequest request) {
		return ServerResponse.ok().body(useCase.getAll(), Franchise.class);
	}

}
