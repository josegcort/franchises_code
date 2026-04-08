package com.tt.franchises.domain.port;

import com.tt.franchises.domain.model.Franchise;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
	Mono<Franchise> save(Franchise franchise);

	Mono<Franchise> findById(String id);

	Mono<Franchise> findByNameIgnoreCase(String name);
	
	Flux<Franchise> findAll();
}
