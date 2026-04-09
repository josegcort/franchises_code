package com.tt.franchises.infrastructure.adapter.out.mongo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.tt.franchises.domain.model.Product;
import com.tt.franchises.infrastructure.adapter.out.mongo.document.ProductDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MongoDB repository for managing Product documents.
 */
public interface ProductMongoRepository extends ReactiveMongoRepository<ProductDocument, String> {
	Mono<ProductDocument> findByNameIgnoreCase(String name);

	Mono<ProductDocument> findByNameIgnoreCaseAndBranchId(String name, String branchId);

	Flux<ProductDocument> findByBranchId(String branchId);

	Mono<Product> findTopByBranchIdOrderByStockDesc(String branchId);

}