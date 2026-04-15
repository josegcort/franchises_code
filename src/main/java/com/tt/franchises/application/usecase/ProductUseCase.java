package com.tt.franchises.application.usecase;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.application.dto.TopStockProductResponse;
import com.tt.franchises.domain.model.Product;
import com.tt.franchises.domain.port.BranchRepository;
import com.tt.franchises.domain.port.FranchiseRepository;
import com.tt.franchises.domain.port.ProductRepository;
import com.tt.franchises.tools.Operations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use case for managing products.
 */
@Slf4j
@Component
@AllArgsConstructor
public class ProductUseCase {

	private final ProductRepository productRepo;
	private final BranchRepository branchRepo;
	private final FranchiseRepository franchiseRepo;
	private final MessageSource msgSrc;

	// Create a new product
	public Mono<Product> create(Product product) {

		// Validate that the branchId is not null or empty
		if (Operations.validateString(product.getBranchId())) {
			String error = Operations.getMessage(msgSrc, "error.product.branchId.required");

			log.error(error);

			return Mono.error(//
					new ResponseStatusException(HttpStatus.BAD_REQUEST, error)//
			);
		}

		// Validate that the name is not null or empty
		if (Operations.validateString(product.getName())) {
			String error = Operations.getMessage(msgSrc, "error.product.name.required");

			log.error(error);

			return Mono.error(//
					new ResponseStatusException(HttpStatus.BAD_REQUEST, error)//
			);
		}

		// Validate that the stock is not null
		if (Operations.validateInteger(product.getStock())) {
			String error = Operations.getMessage(msgSrc, "error.product.stock.required");

			log.error(error);

			return Mono.error(//
					new ResponseStatusException(HttpStatus.BAD_REQUEST, error)//
			);
		}
		
		// Validate that the stock is negative
		if (Operations.validateIntegerNegative(product.getStock())) {
			String error = Operations.getMessage(msgSrc, "error.product.stock.positive");

			log.error(error);

			return Mono.error(//
					new ResponseStatusException(HttpStatus.BAD_REQUEST, error)//
			);
		}

		return branchRepo.findById(product.getBranchId())
				// Validate that the branch exists
				.switchIfEmpty(//
						Mono.error(//
								new ResponseStatusException(//
										HttpStatus.NOT_FOUND, Operations.getMessage(msgSrc, "error.branch.notFoundById")//
								)))

				// Validate that the name is unique
				.flatMap(branch -> //
				productRepo.findByNameIgnoreCaseAndBranchId(product.getName(), product.getBranchId())//
						.hasElement()//
						.flatMap(exists -> {//
							if (exists) {
								return Mono.error(//
										new ResponseStatusException(//
												HttpStatus.CONFLICT, //
												Operations.getMessage(msgSrc, "error.product.name.duplicate")//
								));
							}

							return productRepo.save(product);
						}));
	}

	// Update the stock of a product
	public Mono<Product> updateStock(String id, Integer newStock) {

		// Validate that the stock is not null
		if (Operations.validateInteger(newStock)) {
			String error = Operations.getMessage(msgSrc, "error.product.stock.required");

			log.error(error);

			return Mono.error(//
					new ResponseStatusException(HttpStatus.BAD_REQUEST, error)//
			);
		}

		// Validate that the stock is negative
		if (Operations.validateIntegerNegative(newStock)) {
			String error = Operations.getMessage(msgSrc, "error.product.stock.positive");

			log.error(error);

			return Mono.error(//
					new ResponseStatusException(HttpStatus.BAD_REQUEST, error)//
			);
		}

		return productRepo.findById(id)//
				.switchIfEmpty(//
						Mono.error(//
								new ResponseStatusException(//
										HttpStatus.NOT_FOUND,
										Operations.getMessage(msgSrc, "error.product.notFoundById")//
								)))//
				.flatMap(product -> {//
					//Update stock
					product.setStock(newStock);
					return productRepo.save(product);
				});
	}

	// Delete a product by ID
	public Mono<Void> delete(String id) {
		return productRepo.findById(id)//
				.switchIfEmpty(//
						Mono.error(//
								new ResponseStatusException(//
										HttpStatus.NOT_FOUND,
										Operations.getMessage(msgSrc, "error.product.notFoundById")//
								)))//
				.flatMap(//
						product -> productRepo.delete(product.getId())//
				);
	}

	// Get a product by ID
	public Mono<Product> getById(String id) {
		return productRepo.findById(id).switchIfEmpty(//
				Mono.error(//
						new ResponseStatusException(//
								HttpStatus.NOT_FOUND, Operations.getMessage(msgSrc, "error.product.notFoundById")//
						)//
				));
	}

	// Get all products
	public Flux<Product> getAll() {
		return productRepo.findAll();
	}

	// Get a product by branchId
	public Flux<Product> getByBranchId(String branchId) {
		return productRepo.findByBranchId(branchId);
	}

	// Get the product with the most stock for each branch of a franchise
	public Flux<TopStockProductResponse> getTopStockPerBranch(String franchiseId) {
		return franchiseRepo.findById(franchiseId)//
				// The franchise must exist
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						Operations.getMessage(msgSrc, "error.franchise.notFoundById"))))
				// For each branch of that franchise, look for the product with the most stock.
				.flatMapMany(//
						f -> branchRepo.findByFranchiseId(franchiseId)//
				)//
				.flatMap(branch -> productRepo.findTopByBranchIdOrderByStockDesc(//
						branch.getId()//
				)// If the branch doesn't have products, it skips it instead of failing.
						.map(product -> new TopStockProductResponse(//
								branch.getId(), //
								branch.getName(), //
								product.getId(), //
								product.getName(), //
								product.getStock()//
						)));
	}

}
