package com.tt.franchises.infrastructure.adapter.out.mongo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.tt.franchises.infrastructure.adapter.out.mongo.document.FranchiseDocument;

import reactor.core.publisher.Mono;

public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {

	Mono<FranchiseDocument> findByNameIgnoreCase(String name);

}