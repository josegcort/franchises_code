package com.tt.franchises.infrastructure.adapter.out.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {

	Mono<FranchiseDocument> findByNameIgnoreCase(String name);

}