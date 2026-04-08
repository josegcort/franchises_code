package com.tt.franchises.application.usecase;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.domain.port.FranchiseRepository;
import com.tt.franchises.tools.Operations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use case for managing franchises.
 */
@Slf4j
@Component
@AllArgsConstructor
public class FranchiseUseCase {

	private final FranchiseRepository repo;
	private final MessageSource msgSrc;

	// Create a new franchise
	public Mono<Franchise> create(Franchise franchise) {

		// Validate that the name is not null or empty
		if (Operations.validateString(franchise.getName())) {
			String error = Operations.getMessage(msgSrc, "error.franchise.name.required");

			log.error(error);

			return Mono.error(new ResponseStatusException(//
					HttpStatus.BAD_REQUEST, error//
			));
		}

		// Validate that the name is unique
		return repo.findByNameIgnoreCase(franchise.getName())//
				.hasElement()//
				.flatMap(exists -> {//
					if (exists) {//
						String error = Operations.getMessage(msgSrc, "error.franchise.name.duplicate");

						log.error(error);

						return Mono.error(//
								new ResponseStatusException(//
										HttpStatus.CONFLICT, error//
						));//
					} //
					return repo.save(franchise);//
				});
	}

	// Get a franchise by ID
	public Mono<Franchise> getById(String id) {
		return repo.findById(id).switchIfEmpty(//
				Mono.error(//
						new ResponseStatusException(//
								HttpStatus.NOT_FOUND, Operations.getMessage(msgSrc, "error.franchise.notFoundById")//
						)//
				));
	}

	// Get all franchises
	public Flux<Franchise> getAll() {
		return repo.findAll();
	}

}
