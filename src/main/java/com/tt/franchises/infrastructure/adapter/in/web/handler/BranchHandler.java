package com.tt.franchises.infrastructure.adapter.in.web.handler;

import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.application.dto.BranchRequest;
import com.tt.franchises.application.usecase.BranchUseCase;
import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.tools.Operations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Handler for managing branch-related HTTP requests.
 */
@Slf4j
@Component
@AllArgsConstructor
public class BranchHandler {

	private final BranchUseCase useCase;
	private final Validator validator;
	private final MessageSource msgSrc;

	// Handler method for creating a new branch
	public Mono<ServerResponse> create(ServerRequest request) {

		return request.bodyToMono(BranchRequest.class)//
				.flatMap(req -> {
					
					// Validate the request using the Validator
					Set<ConstraintViolation<BranchRequest>> violations = validator.validate(req);

					// If there are validation errors, return a 400 Bad Request response with the
					// error message
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

					return useCase.create(new Branch(null, req.getName(), req.getFranchiseId()));
				})//
				.flatMap(branch -> ServerResponse//
						.status(HttpStatus.CREATED)//
						.bodyValue(branch));
	}

	// Handler method for returning a branch by its ID
	public Mono<ServerResponse> getById(ServerRequest request) {
		String id = request.pathVariable("id");
		return useCase.getById(id)//
				.flatMap(b -> ServerResponse.ok().bodyValue(b));
	}

	// Handler method for returning a branches by its FranchiseId
	public Mono<ServerResponse> getByFranchiseId(ServerRequest request) {
		String franchiseId = request.pathVariable("franchiseId");
		return ServerResponse.ok().body(useCase.getByFranchiseId(franchiseId), Branch.class);
	}

	
	// Handler method for returning all branches
	public Mono<ServerResponse> getAll(ServerRequest request) {
		return ServerResponse.ok().body(useCase.getAll(), Branch.class);
	}

}
