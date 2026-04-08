package com.tt.franchises.application.usecase;

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

@Slf4j
@Component
@AllArgsConstructor
public class FranchiseUseCase {

	private final FranchiseRepository repo;

	public Mono<Franchise> create(Franchise franchise) {

		// Validate that the name is not null or empty
		if (Operations.validateString(franchise.getName())) {
			String error = "El nombre de la franquicia es obligatorio.";

			return Mono.error(new ResponseStatusException(//
					HttpStatus.BAD_REQUEST, error//
			));
		}

		// Validate that the name is unique
		return repo.findByNameIgnoreCase(franchise.getName())//
				.hasElement()//
				.flatMap(exists -> {//
					if (exists) {//
						String error = "Ya existe una franquicia con este nombre.";
						log.error(error);
						return Mono.error(//
								new ResponseStatusException(//
										HttpStatus.CONFLICT, error//
						));//
					} //
					return repo.save(franchise);//
				});
	}

	public Mono<Franchise> getById(String id) {
		return repo.findById(id).switchIfEmpty(//
				Mono.error(//
						new ResponseStatusException(//
								HttpStatus.NOT_FOUND, "No se encontro una franquicia con este ID."//
						)//
				));
	}

	public Flux<Franchise> getAll() {
		return repo.findAll();
	}

}
