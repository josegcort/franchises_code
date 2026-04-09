package com.tt.franchises.infrastructure.adapter.out.mongo.adapter;

import org.springframework.stereotype.Component;

import com.tt.franchises.domain.model.Product;
import com.tt.franchises.domain.port.ProductRepository;
import com.tt.franchises.infrastructure.adapter.out.mongo.mapper.ProductMapper;
import com.tt.franchises.infrastructure.adapter.out.mongo.repository.ProductMongoRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter for the ProductRepository that uses MongoDB as the data store.
 */
@Component
@AllArgsConstructor
public class ProductMongoAdapter implements ProductRepository {

	private final ProductMongoRepository repo;
	private final ProductMapper mapper;

	// Save a product to the database and return the saved product.
	@Override
	public Mono<Product> save(Product product) {
		return repo.save(mapper.toDocument(product)).map(mapper::toDomain);
	}

	// Find a product by its ID and return it if found.
	@Override
	public Mono<Product> findById(String id) {
		return repo.findById(id).map(mapper::toDomain);
	}

	// Find all products in the database and return them as a Flux.
	@Override
	public Flux<Product> findAll() {
		return repo.findAll().map(mapper::toDomain);
	}

	//  Find a branch by branchId and return them as a Flux.
	@Override
	public Flux<Product> findByBranchId(String branchId) {
		return repo.findByBranchId(branchId).map(mapper::toDomain);
	}

	// Find a product by its name and branchId and return it if found.
	@Override
	public Mono<Product> findByNameIgnoreCaseAndBranchId(String name, String branchId) {
		return repo.findByNameIgnoreCaseAndBranchId(name, branchId).map(mapper::toDomain);
	}

	// Delete a product by its ID.
	@Override
	public Mono<Void> delete(String id) {
		 return repo.deleteById(id);
	}

	// Find the product with the most stock for a given branchId and return it if found.
	@Override
	public Mono<Product> findTopByBranchIdOrderByStockDesc(String branchId) {
		return repo.findTopByBranchIdOrderByStockDesc(branchId);
	}

}
