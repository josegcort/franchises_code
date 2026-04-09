package com.tt.franchises.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.application.dto.ProductRequest;
import com.tt.franchises.application.dto.ProductStockRequest;
import com.tt.franchises.application.usecase.ProductUseCase;
import com.tt.franchises.domain.model.Product;
import com.tt.franchises.infrastructure.adapter.in.web.handler.ProductHandler;
import com.tt.franchises.infrastructure.adapter.in.web.router.ProductRouter;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Unit Tests for ProductHandlerTest
 */
@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

	// WebTestClient to perform HTTP requests in tests
	private WebTestClient client;

	// Create a Validator instance for testing
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Mock
	private MessageSource msgSrc;

	@Mock
	private ProductUseCase useCase;

	// variable global franchise branchId for tests
	private final String branchId = "321abc";

	// Set up the WebTestClient with the ProductHandler and ProductRouter
	@BeforeEach
	void setUp() {
		ProductHandler handler = new ProductHandler(useCase, validator, msgSrc);
		ProductRouter router = new ProductRouter();

		// Build the WebTestClient with the router function
		client = WebTestClient//
				.bindToRouterFunction(router.productRoutes(handler))//
				.build();
	}

	/**
	 * Test Create
	 */
	// Test case for creating a product successfully
	@Test
	void create_shouldSave_shouldReturn201() {
		Product itemSaved = new Product("123xyz", "Carne", 22, branchId);

		when(useCase.create(any(Product.class))).thenReturn(Mono.just(itemSaved));

		ProductRequest productRequest = new ProductRequest("Carne", branchId, 33);

		client.post()//
				.uri("/products")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(productRequest)//
				.exchange()//
				.expectStatus().isCreated()//
				.expectBody()//
				.jsonPath("$.id").isEqualTo(itemSaved.getId())//
				.jsonPath("$.name").isEqualTo(itemSaved.getName())//
				.jsonPath("$.branchId").isEqualTo(branchId);

	}

	// Test case for creating a product with an null or empty name, expecting a 400
	// Bad Request response
	@Test
	void create_whenNameIsEmpty_shouldReturn400() {
		ProductRequest productRequest = new ProductRequest("", branchId, 22);

		client.post()//
				.uri("/products")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(productRequest)//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for creating a product with an null or empty branchId, expecting a
	// 400 Bad Request response
	@Test
	void create_whenBranchIdIsEmpty_shouldReturn400() {
		ProductRequest productRequest = new ProductRequest("Carne", "", 22);

		client.post()//
				.uri("/products")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(productRequest)//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for creating a product with a negative stock, expecting a 400 Bad
	// Request response
	@Test
	void create_whenStockIsNull_shouldReturn400() {
		ProductRequest productRequest = new ProductRequest("Carne", "", null);

		client.post()//
				.uri("/products")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(productRequest)//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for creating a product with a negative stock, expecting a 400 Bad
	// Request response
	@Test
	void create_whenStockIsNegative_shouldReturn400() {
		ProductRequest productRequest = new ProductRequest("Carne", "", -35);

		client.post()//
				.uri("/products")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(productRequest)//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for creating a product with a non-existent branchId, expecting a
	// 404 Not Found response
	@Test
	void create_whenBranchDoesNotExist_shouldReturn404() {
		when(useCase.create(any(Product.class))).thenReturn(//
				Mono.error(//
						new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró un producto con este ID.")));

		client.post()//
				.uri("/products")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(new ProductRequest("Carne", branchId, 22))//
				.exchange()//
				.expectStatus().isNotFound();
	}

	// Test case for creating a product with a duplicate name, expecting a 409
	@Test
	void create_whenItAlreadyExists_shouldReturn409() {
		Product itemNew = new Product(null, "Carne", 22, branchId);

		when(useCase.create(itemNew)).thenReturn(Mono.error(//
				new ResponseStatusException(//
						HttpStatus.CONFLICT, "Ya existe un producto con este nombre en esa misma sucursal.")//
		));

		ProductRequest productRequest = new ProductRequest("Carne", branchId, 22);

		client.post().uri("/products")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(productRequest)//
				.exchange()//
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);
	}

	/**
	 * Test FindById
	 */
	// Test case for finding a product by ID that does not exist, expecting a 404
	// Not Found response
	@Test
	void getById_whenItDoesNotExist_shouldReturn404() {
		when(useCase.getById("123xyz")).thenReturn(Mono.error(//
				new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró un producto con este ID.")//
		));

		client.get().uri("/products/123xyz")//
				.exchange()//
				.expectStatus().isNotFound();
	}

	// Test case for finding a product by ID that exists, expecting a 200 OK
	// response with the correct data
	@Test
	void getById_whenItExists_shouldReturn200() {
		Product itemSaved = new Product("123xyz", "Carne", 22, branchId);

		when(useCase.getById("123xyz")).thenReturn(Mono.just(itemSaved));

		client.get().uri("/products/123xyz")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$.name").isEqualTo("Carne");
	}

	/**
	 * Test FindAll
	 */
	// Test case for finding all product when some exist, expecting a 200 OK and
	// array of product
	@Test
	void getAll_shouldReturn200() {
		Product item1 = new Product("123xyz", "Carne", 22, branchId);
		Product item2 = new Product("123abc", "Pollo", 19, branchId);

		when(useCase.getAll()).thenReturn(Flux.just(item1, item2));

		client.get().uri("/products")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(2)//
				.jsonPath("$[0].id").isEqualTo("123xyz")//
				.jsonPath("$[1].id").isEqualTo("123abc");
	}

	// Test case for finding all product when none exist, expecting a 200 OK and
	// an empty array
	@Test
	void getAll_whenNoProductsExist_shouldReturn200() {
		when(useCase.getAll()).thenReturn(Flux.empty());

		client.get().uri("/products")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(0);
	}

	/**
	 * Test findByBranchId
	 */
	// Test case for finding products by branchId when some exist, expecting a 200
	// OK and array of product
	@Test
	void findByBranchId_shouldReturn200() {
		Product item1 = new Product("123xyz", "Carne", 22, branchId);
		Product item2 = new Product("123abc", "Pollo", 19, branchId);

		when(useCase.getByBranchId(branchId)).thenReturn(Flux.just(item1, item2));

		client.get().uri("/branches/{branchId}/products", branchId)//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(2)//
				.jsonPath("$[0].id").isEqualTo("123xyz")//
				.jsonPath("$[1].id").isEqualTo("123abc");
	}

	// Test case for finding products by branchId when none exist, expecting a 200
	// OK and an empty array
	@Test
	void findByBranchId_whenNoProductsExist_shouldReturn200() {
		when(useCase.getByBranchId(branchId)).thenReturn(Flux.empty());

		client.get().uri("/branches/{branchId}/products", branchId)//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(0);
	}

	/**
	 * Test UpdateStock
	 */
	// Test case for updating the stock of a product that exists, expecting a 200 OK
	@Test
	void updateStock_whenExists_shouldReturn200() {
		Product updated = new Product("123xyz", "Carne", 31, branchId);

		when(useCase.updateStock("123xyz", 31)).thenReturn(Mono.just(updated));

		ProductStockRequest stockRequest = new ProductStockRequest(31);
		client.patch()//
				.uri("/products/123xyz/stock")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(stockRequest)//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$.stock").isEqualTo(31);
	}

	// Test case for updating the stock of a product with a null stock value, expecting a 400 Bad Request response
	@Test
	void updateStock_whenStockIsNegative_shouldReturn400() {
		client.patch()//
				.uri("/products/123xyz/stock")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(new ProductStockRequest(-1))//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for updating the stock of a product with a null stock value, expecting a 400 Bad Request response
	@Test
	void updateStock_whenStockIsNull_shouldReturn400() {
		client.patch()//
				.uri("/products/123xyz/stock")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(new ProductStockRequest(null))//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for updating the stock of a product that does not exist, expecting a 404 Not Found response
	@Test
	void updateStock_whenDoesNotExist_shouldReturn404() {
		when(useCase.updateStock("123xyz", 22)).thenReturn(Mono.error(//
				new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró un producto con este ID.")//
		));

		client.patch()//
				.uri("/products/123xyz/stock")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(new ProductStockRequest(22))//
				.exchange()//
				.expectStatus().isNotFound();
	}

	/**
	 * Test Delete
	 */
	// Test case for deleting a product that exists, expecting a 204 No Content response
	@Test
	void delete_whenExists_shouldReturn204() {
		when(useCase.delete("123xyz")).thenReturn(Mono.empty());

		client.delete()//
				.uri("/products/123xyz")//
				.exchange()//
				.expectStatus().isNoContent();
	}

	// Test case for deleting a product that does not exist, expecting a 404 Not Found response
	@Test
	void delete_whenDoesNotExist_shouldReturn404() {
		when(useCase.delete("123xyz")).thenReturn(Mono.error(//
				new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró un producto con este ID.")//
		));

		client.delete()//
				.uri("/products/123xyz")//
				.exchange()//
				.expectStatus().isNotFound();
	}
}