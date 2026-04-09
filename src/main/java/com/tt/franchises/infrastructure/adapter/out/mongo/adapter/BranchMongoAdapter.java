package com.tt.franchises.infrastructure.adapter.out.mongo.adapter;

import org.springframework.stereotype.Component;

import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.domain.port.BranchRepository;
import com.tt.franchises.infrastructure.adapter.out.mongo.mapper.BranchMapper;
import com.tt.franchises.infrastructure.adapter.out.mongo.repository.BranchMongoRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter for the BranchRepository that uses MongoDB as the data store.
 */
@Component
@AllArgsConstructor
public class BranchMongoAdapter implements BranchRepository {

	private final BranchMongoRepository repo;
	private final BranchMapper mapper;

	// Save a branch to the database and return the saved branch.
	@Override
	public Mono<Branch> save(Branch branch) {
		return repo.save(mapper.toDocument(branch)).map(mapper::toDomain);
	}

	// Find a branch by its ID and return it if found.
	@Override
	public Mono<Branch> findById(String id) {
		return repo.findById(id).map(mapper::toDomain);
	}

	// Find all branchs in the database and return them as a Flux.
	@Override
	public Flux<Branch> findAll() {
		return repo.findAll().map(mapper::toDomain);
	}

	//  Find a branch by franchiseId and return them as a Flux.
	@Override
	public Flux<Branch> findByFranchiseId(String franchiseId) {
		return repo.findByFranchiseId(franchiseId).map(mapper::toDomain);
	}

	// Find a branch by its name and franchiseId and return it if found.
	@Override
	public Mono<Branch> findByNameIgnoreCaseAndFranchiseId(String name, String franchiseId) {
		return repo.findByNameIgnoreCaseAndFranchiseId(name, franchiseId).map(mapper::toDomain);
	}

}
