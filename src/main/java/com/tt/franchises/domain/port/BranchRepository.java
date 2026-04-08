package com.tt.franchises.domain.port;

import com.tt.franchises.domain.model.Branch;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing branch entities.
 */
public interface BranchRepository {
	Mono<Branch> save(Branch branch);

	Mono<Branch> findById(String id);

	Mono<Branch> findByNameIgnoreCaseAndFranchiseId(String name, String franchiseId);

	Flux<Branch> findByFranchiseId(String franchiseId);

	Flux<Branch> findAll();

}
