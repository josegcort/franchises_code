package com.tt.franchises.infrastructure.adapter.in.web;

import java.time.Instant;

import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.tt.franchises.tools.ErrorResponse;
import com.tt.franchises.tools.Operations;

import reactor.core.publisher.Mono;

/**
 * Global exception handler for handling errors in the web flux application.
 */
@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

		String message = ex.getMessage();
		HttpStatusCode status = exchange.getResponse().getStatusCode();
		String statusCode = status.toString();
		String error = status.toString();

		// If the exception is a ResponseStatusException, we can extract more information from it.
		if (ex instanceof ResponseStatusException rse) {
			HttpStatus statusValue = HttpStatus.valueOf(rse.getStatusCode().value());
			status = rse.getStatusCode();
			message = rse.getReason();
			statusCode = String.valueOf(statusValue.value());
			error = statusValue.getReasonPhrase();
		}

		// Set the response status and content type.
		exchange.getResponse().setStatusCode(status);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

		// Create an error response object to send.
		ErrorResponse response = new ErrorResponse(//
				error, //
				statusCode, //
				message, //
				exchange.getRequest().getPath().value(), //
				exchange.getRequest().getId(), //
				Instant.now().toString()//
		);

		// Convert the error response object to JSON and write it to the response body.
		String body = Operations.convertObjectToJsonString(response);
		byte[] bytes = body.getBytes();

		return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
	}
}