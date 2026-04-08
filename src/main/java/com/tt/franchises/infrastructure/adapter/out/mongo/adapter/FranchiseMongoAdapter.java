package com.tt.franchises.infrastructure.adapter.out.mongo.adapter;

import org.springframework.stereotype.Component;

import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.domain.port.FranchiseRepository;
import com.tt.franchises.infrastructure.adapter.out.mongo.mapper.FranchiseMapper;
import com.tt.franchises.infrastructure.adapter.out.mongo.repository.FranchiseMongoRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter for the FranchiseRepository that uses MongoDB as the data store.
 */
@Component
@AllArgsConstructor
public class FranchiseMongoAdapter implements FranchiseRepository {

	private final FranchiseMongoRepository repo;
	private final FranchiseMapper mapper;

	// Save a franchise to the database and return the saved franchise.
	@Override
	public Mono<Franchise> save(Franchise franchise) {
		return repo.save(mapper.toDocument(franchise)).map(mapper::toDomain);
	}

	// Find a franchise by its ID and return it if found.
	@Override
	public Mono<Franchise> findById(String id) {
		return repo.findById(id).map(mapper::toDomain);
	}

	// Find all franchises in the database and return them as a Flux.
	@Override
	public Flux<Franchise> findAll() {
		return repo.findAll().map(mapper::toDomain);
	}

	// Find a franchise by its name (case-insensitive) and return it if found.
	@Override
	public Mono<Franchise> findByNameIgnoreCase(String name) {
		return repo.findByNameIgnoreCase(name).map(mapper::toDomain);
	}

}
