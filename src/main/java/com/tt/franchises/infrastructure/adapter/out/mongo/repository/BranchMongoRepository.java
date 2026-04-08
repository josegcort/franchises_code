package com.tt.franchises.infrastructure.adapter.out.mongo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.tt.franchises.infrastructure.adapter.out.mongo.document.BranchDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MongoDB repository for managing Branch documents.
 */
public interface BranchMongoRepository extends ReactiveMongoRepository<BranchDocument, String> {
	Mono<BranchDocument> findByNameIgnoreCase(String name);

	Mono<BranchDocument> findByNameIgnoreCaseAndFranchiseId(String name, String franchiseId);

	Flux<BranchDocument> findByFranchiseId(String franchiseId);

}