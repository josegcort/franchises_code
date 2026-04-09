package com.tt.franchises.domain.port;

import com.tt.franchises.domain.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing product entities.
 */
public interface ProductRepository {
	Mono<Product> save(Product product);

	Mono<Product> findById(String id);

	Mono<Void> delete(String id);

	Mono<Product> findByNameIgnoreCaseAndBranchId(String name, String branchId);

	Flux<Product> findByBranchId(String branchId);

	Flux<Product> findAll();

}
