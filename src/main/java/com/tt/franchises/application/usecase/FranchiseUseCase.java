package com.tt.franchises.application.usecase;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.domain.port.FranchiseRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class FranchiseUseCase {

	private final FranchiseRepository repo;

	public Mono<Franchise> create(Franchise franchise) {
		return repo.findByNameIgnoreCase(franchise.getName())//
				.hasElement()//
				.flatMap(exists -> {//
					if (exists) {//
						return Mono.error(//
								new ResponseStatusException(//
										HttpStatus.CONFLICT, //
										"Ya existe una franquicia con este nombre."//
						));//
					} //
					return repo.save(franchise);//
				});
	}

	public Mono<Franchise> getById(String id) {
		return repo.findById(id).switchIfEmpty(//
				Mono.error(//
						new ResponseStatusException(//
								HttpStatus.NOT_FOUND, //
								"No se encontro una franquicia con este ID."//
						)//
				));
	}

	public Flux<Franchise> getAll() {
		return repo.findAll();
	}

}
