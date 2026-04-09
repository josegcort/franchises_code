package com.tt.franchises.infrastructure.adapter.in.web.handler;

import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.application.dto.ProductRequest;
import com.tt.franchises.application.dto.ProductStockRequest;
import com.tt.franchises.application.dto.TopStockProductResponse;
import com.tt.franchises.application.usecase.ProductUseCase;
import com.tt.franchises.domain.model.Product;
import com.tt.franchises.tools.Operations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Handler for managing product-related HTTP requests.
 */
@Slf4j
@Component
@AllArgsConstructor
public class ProductHandler {

	private final ProductUseCase useCase;
	private final Validator validator;
	private final MessageSource msgSrc;

	// Handler method for creating a new product
	public Mono<ServerResponse> create(ServerRequest request) {

		return request.bodyToMono(ProductRequest.class)//
				.flatMap(req -> {

					// Validate the request using the Validator
					Set<ConstraintViolation<ProductRequest>> violations = validator.validate(req);

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

					return useCase.create(new Product(null, req.getName(), req.getStock(), req.getBranchId()));
				})//
				.flatMap(product -> ServerResponse//
						.status(HttpStatus.CREATED)//
						.bodyValue(product));
	}

	// Handler method for updating a product stock
	public Mono<ServerResponse> updateStock(ServerRequest request) {
		String id = request.pathVariable("id");

		return request.bodyToMono(ProductStockRequest.class)//
				.flatMap(req -> {//

					// Validate the request using the Validator
					Set<ConstraintViolation<ProductStockRequest>> violations = validator.validate(req);

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
					return useCase.updateStock(id, req.getStock());
				}).flatMap(product -> ServerResponse.ok().bodyValue(product));
	}

	// Handler method for deleting a product
	public Mono<ServerResponse> delete(ServerRequest request) {
		String id = request.pathVariable("id");
		return useCase.delete(id)//
				.then(ServerResponse.noContent().build());
	}

	// Handler method for returning a product by its ID
	public Mono<ServerResponse> getById(ServerRequest request) {
		String id = request.pathVariable("id");
		return useCase.getById(id)//
				.flatMap(p -> ServerResponse.ok().bodyValue(p));
	}

	// Handler method for returning a products by its BranchId
	public Mono<ServerResponse> getByBranchId(ServerRequest request) {
		String branchId = request.pathVariable("branchId");
		return ServerResponse.ok().body(useCase.getByBranchId(branchId), Product.class);
	}

	// Handler method for returning all products
	public Mono<ServerResponse> getAll(ServerRequest request) {
		return ServerResponse.ok().body(useCase.getAll(), Product.class);
	}

	// Handler method for returning the product with the most stock for each branch of a franchise
	public Mono<ServerResponse> getTopStockPerBranch(ServerRequest request) {
		String franchiseId = request.pathVariable("franchiseId");
		return ServerResponse.ok()//
				.body(useCase.getTopStockPerBranch(franchiseId), TopStockProductResponse.class);
	}
}
