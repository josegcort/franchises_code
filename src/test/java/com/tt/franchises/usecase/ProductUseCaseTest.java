package com.tt.franchises.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.application.usecase.ProductUseCase;
import com.tt.franchises.domain.model.Product;
import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.domain.port.ProductRepository;
import com.tt.franchises.domain.port.BranchRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit Tests for ProductUseCase
 */
@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

	@Mock
	private ProductRepository productRepo;

	@Mock
	private BranchRepository branchRepo;

	@Mock
	private MessageSource msgSrc;

	@InjectMocks
	private ProductUseCase useCase;

	// variable global branch and branchId for tests
	private final String branchId = "321abc";
	private final Branch branch = new Branch(branchId, "AXM", "321cba");

	/**
	 * Test Create
	 */
	// Test to validate the correct creation of a product
	@Test
	void create_shouldSaveAndReturnProduct() {
		Product itemNew = new Product(null, "Carne de cerdo", 22, branchId);
		Product itemSaved = new Product("123xyz", "Carne de cerdo", 22, branchId);

		when(branchRepo.findById(branchId)).thenReturn(Mono.just(branch));
		when(productRepo.findByNameIgnoreCaseAndBranchId("Carne de cerdo", branchId)).thenReturn(Mono.empty());
		when(productRepo.save(any(Product.class))).thenReturn(Mono.just(itemSaved));

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectNextMatches(//
						p -> p.getId().equals("123xyz")//
								&& p.getName().equals("Carne de cerdo"))//
				.verifyComplete();
	}

	// Test to validate that creating a product with a null or empty name returns
	// a bad request error
	@Test
	void create_whenNameIsEmpty_shouldReturnBadRequest() {
		Product itemNew = new Product(null, null, 22, branchId);

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)//
				.verify();
	}

	// Test to validate that creating a product with a null stock returns a
	// Bad Request response
	@Test
	void create_whenStockIsNull_shouldReturnBadRequest() {
		Product itemNew = new Product(null, "Carne", null, branchId);

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)//
				.verify();
	}

	// Test to validate that creating a product with a negative stock returns a
	// Bad Request response
	@Test
	void create_whenStockIsNegative_shouldReturnBadRequest() {
		Product itemNew = new Product(null, "Carne", -20, branchId);

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)//
				.verify();
	}

	// Test case for creating a product with an null or empty branch, expecting a
	// Bad Request response
	@Test
	void create_whenBranchIsNEmpty_shouldReturnBadRequest() {
		Product itemNew = new Product(null, "Carne", 22, "");

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)
				.verify();
	}

	// Test to validate that creating a product for a non-existent branch returns
	// a not found error
	@Test
	void create_whenBranchDoesNotExist_shouldReturnNotFound() {
		Product itemNew = new Product(null, "Carne", 22, branchId);

		when(branchRepo.findById(branchId)).thenReturn(Mono.empty());

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.NOT_FOUND)
				.verify();
	}

	// Test to validate that creating a product with an existing name returns a
	// conflict error
	@Test
	void create_whenItAlreadyExists_shouldReturnConflict() {
		Product itemOld = new Product("123xyz", "Carne", 22, branchId);
		Product itemNew = new Product(null, "Carne", 22, branchId);

		when(branchRepo.findById(branchId)).thenReturn(Mono.just(branch));
		when(productRepo.findByNameIgnoreCaseAndBranchId("Carne", branchId)).thenReturn(Mono.just(itemOld));

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.CONFLICT)//
				.verify();
	}

	/**
	 * Test FindById
	 */
	// Test to validate that searching for a non-existent product by ID returns a
	// not found error
	@Test
	void getById_whenItDoesNotExist_shouldReturnNotFound() {
		when(productRepo.findById(anyString())).thenReturn(Mono.empty());

		StepVerifier.create(//
				useCase.getById("123xyz"))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.NOT_FOUND //
				).verify();
	}

	// Test to validate that searching for an existing product by ID returns the
	// product
	@Test
	void findById_whenItExists_shouldReturnProduct() {
		Product itemSaved = new Product("123xyz", "Carne", 22, branchId);

		when(productRepo.findById("123xyz")).thenReturn(Mono.just(itemSaved));

		StepVerifier.create(//
				useCase.getById("123xyz"))//
				.expectNextMatches(//
						f -> f.getId().equals("123xyz"))//
				.verifyComplete();
	}

	/**
	 * Test FindAll
	 */
	// Test to validate that searching for all product returns the correct list
	// of product
	@Test
	void findAll_shouldReturnAllProducts() {
		Product item1 = new Product("123xyz", "Carne", 22, branchId);
		Product item2 = new Product("123abc", "Pollo", 18, branchId);

		when(productRepo.findAll()).thenReturn(Flux.just(item1, item2));

		StepVerifier.create(//
				useCase.getAll())//
				.expectNextMatches(//
						p -> p.getName().equals("Carne")//
				).expectNextMatches(//
						p -> p.getName().equals("Pollo"))//
				.verifyComplete();
	}

	// Test to validate that searching for all products returns an empty list when
	// no products exist
	@Test
	void findAll_whenNoProductsExist_shouldReturnEmptyFlux() {
		when(productRepo.findAll()).thenReturn(Flux.empty());

		StepVerifier.create(//
				useCase.getAll())//
				.expectNextCount(0)//
				.verifyComplete();
	}

	/**
	 * Test FindByBranchId
	 */
	// Test to validate that searching for products by branch ID returns the
	// correct list of products for that branch
	@Test
	void findByBranchId_shouldReturnByBranchIdProducts() {
		Product item1 = new Product("123xyz", "Carne", 22, branchId);
		Product item2 = new Product("123abc", "Pollo", 18, branchId);

		when(productRepo.findByBranchId(branchId)).thenReturn(Flux.just(item1, item2));

		StepVerifier.create(//
				useCase.getByBranchId(branchId))//
				.expectNextMatches(//
						p -> p.getName().equals("Carne")//
				).expectNextMatches(//
						p -> p.getName().equals("Pollo"))//
				.verifyComplete();
	}

	// Test to validate that searching for products by branch ID returns an empty
	// list when no products exist for that branch
	@Test
	void findByBranchId_whenNoProductsExist_shouldReturnEmptyFlux() {
		when(productRepo.findByBranchId(branchId)).thenReturn(Flux.empty());

		StepVerifier.create(//
				useCase.getByBranchId(branchId))//
				.expectNextCount(0)//
				.verifyComplete();
	}

	/**
	 * Test UpdateStock
	 */
	// Test to validate that updating the stock of an existing product returns the
	// update product
	@Test
	void updateStock_whenExists_shouldReturnUpdated() {
		Product existing = new Product("123xyz", "Carne", 22, branchId);
		Product updated = new Product("123xyz", "Carne", 31, branchId);

		when(productRepo.findById("123xyz")).thenReturn(Mono.just(existing));
		when(productRepo.save(any(Product.class))).thenReturn(Mono.just(updated));

		StepVerifier.create(//
				useCase.updateStock("123xyz", 31))//
				.expectNextMatches(//
						p -> p.getStock() == 31 //
								&& p.getName().equals("Carne"))//
				.verifyComplete();
	}

	// Test to validate that updating the stock of a non-existent product returns a
	// not found error
	@Test
	void updateStock_whenDoesNotExist_shouldReturnNotFound() {
		when(productRepo.findById("123xyz")).thenReturn(Mono.empty());

		StepVerifier.create(//
				useCase.updateStock("123xyz", 31))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse//
								&& rse.getStatusCode() == HttpStatus.NOT_FOUND)//
				.verify();
	}

	// Test to validate that updating the stock of a product with a negative value
	// returns a bad request error
	@Test
	void updateStock_whenStockIsNegative_shouldReturnBadRequest() {
		StepVerifier.create(//
				useCase.updateStock("123xyz", -1))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse//
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)//
				.verify();
	}

	// Test to validate that updating the stock of a product with a null value
	// returns a bad request error
	@Test
	void updateStock_whenStockIsNull_shouldReturnBadRequest() {
		StepVerifier.create(//
				useCase.updateStock("123xyz", null))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse//
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)//
				.verify();
	}

	/**
	 * Test Delete
	 */
	@Test
	void delete_whenExists_shouldComplete() {
		Product existing = new Product("123xyz", "Pollo", 100, branchId);

		when(productRepo.findById("123xyz")).thenReturn(Mono.just(existing));
		when(productRepo.delete("123xyz")).thenReturn(Mono.empty());

		StepVerifier.create(//
				useCase.delete("123xyz"))//
				.verifyComplete();
	}

	@Test
	void delete_whenDoesNotExist_shouldReturnNotFound() {
		when(productRepo.findById("123xyz")).thenReturn(Mono.empty());

		StepVerifier.create(//
				useCase.delete("123xyz"))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse//
								&& rse.getStatusCode() == HttpStatus.NOT_FOUND)//
				.verify();
	}
}