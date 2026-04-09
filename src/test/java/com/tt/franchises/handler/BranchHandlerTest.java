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

import com.tt.franchises.application.dto.BranchRequest;
import com.tt.franchises.application.usecase.BranchUseCase;
import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.infrastructure.adapter.in.web.handler.BranchHandler;
import com.tt.franchises.infrastructure.adapter.in.web.router.BranchRouter;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Unit Tests for BranchHandlerTest
 */
@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

	// WebTestClient to perform HTTP requests in tests
	private WebTestClient client;

	// Create a Validator instance for testing
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Mock
	private MessageSource msgSrc;

	@Mock
	private BranchUseCase useCase;

	// variable global franchise franchiseId for tests
	private final String franchiseId = "321abc";

	// Set up the WebTestClient with the BranchHandler and BranchRouter
	@BeforeEach
	void setUp() {
		BranchHandler handler = new BranchHandler(useCase, validator, msgSrc);
		BranchRouter router = new BranchRouter();

		// Build the WebTestClient with the router function
		client = WebTestClient//
				.bindToRouterFunction(router.branchRoutes(handler))//
				.build();
	}

	/**
	 * Test Create
	 */
	// Test case for creating a branch successfully
	@Test
	void create_shouldSave_shouldReturn201() {
		Branch itemSaved = new Branch("123xyz", "Sede Norte", franchiseId);

		when(useCase.create(any(Branch.class))).thenReturn(Mono.just(itemSaved));

		BranchRequest branchRequest = new BranchRequest("Sede Norte", franchiseId);

		client.post()//
				.uri("/branches")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(branchRequest)//
				.exchange()//
				.expectStatus().isCreated()//
				.expectBody()//
				.jsonPath("$.id").isEqualTo(itemSaved.getId())//
				.jsonPath("$.name").isEqualTo(itemSaved.getName())//
				.jsonPath("$.franchiseId").isEqualTo(franchiseId);

	}

	// Test case for creating a branch with an null or empty name, expecting a 400
	// Bad
	// Request response
	@Test
	void create_whenNameIsEmpty_shouldReturn400() {
		BranchRequest branchRequest = new BranchRequest("", franchiseId);

		client.post()//
				.uri("/branches")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(branchRequest)//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for creating a branch with an null or empty franchiseId, expecting a 400 Bad Request response
	@Test
	void create_whenFranchiseIdIsEmpty_shouldReturn400() {
		BranchRequest branchRequest = new BranchRequest("Sede Norte", "");

		client.post()//
				.uri("/branches")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(branchRequest)//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for creating a branch with a non-existent franchiseId, expecting a 404 Not Found response
	@Test
	void create_whenFranchiseDoesNotExist_shouldReturn404() {
		when(useCase.create(any(Branch.class)))
				.thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró una sucursal con este ID.")));

		client.post()//
				.uri("/branches")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(new BranchRequest("Sede Norte", franchiseId))//
				.exchange()//
				.expectStatus().isNotFound();
	}

	// Test case for creating a branch with a duplicate name, expecting a 409
	@Test
	void create_whenItAlreadyExists_shouldReturn409() {
		Branch itemNew = new Branch(null, "Sede Norte", franchiseId);

		when(useCase.create(itemNew)).thenReturn(Mono.error(//
				new ResponseStatusException(//
						HttpStatus.CONFLICT, "Ya existe una sucursal con este nombre en esa misma franquicia.")//
		));

		BranchRequest branchRequest = new BranchRequest("Sede Norte", franchiseId);

		client.post().uri("/branches")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(branchRequest)//
				.exchange()//
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);
	}

	/**
	 * Test FindById
	 */
	// Test case for finding a branch by ID that does not exist, expecting a 404
	// Not Found response
	@Test
	void getById_whenItDoesNotExist_shouldReturn404() {
		when(useCase.getById("123xyz")).thenReturn(Mono.error(//
				new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró una sucursal con este ID.")//
		));

		client.get().uri("/branches/123xyz")//
				.exchange()//
				.expectStatus().isNotFound();
	}

	// Test case for finding a branch by ID that exists, expecting a 200 OK
	// response with the correct data
	@Test
	void getById_whenItExists_shouldReturn200() {
		Branch itemSaved = new Branch("123xyz", "Sede Norte", franchiseId);

		when(useCase.getById("123xyz")).thenReturn(Mono.just(itemSaved));

		client.get().uri("/branches/123xyz")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$.name").isEqualTo("Sede Norte");
	}

	/**
	 * Test FindAll
	 */
	// Test case for finding all branch when some exist, expecting a 200 OK and
	// array of branch
	@Test
	void getAll_shouldReturn200() {
		Branch item1 = new Branch("123xyz", "Sede Norte", franchiseId);
		Branch item2 = new Branch("123abc", "Sede Sur", franchiseId);

		when(useCase.getAll()).thenReturn(Flux.just(item1, item2));

		client.get().uri("/branches")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(2)//
				.jsonPath("$[0].id").isEqualTo("123xyz")//
				.jsonPath("$[1].id").isEqualTo("123abc");
	}

	// Test case for finding all branch when none exist, expecting a 200 OK and
	// an empty array
	@Test
	void getAll_whenNoBranchsExist_shouldReturn200() {
		when(useCase.getAll()).thenReturn(Flux.empty());

		client.get().uri("/branches")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(0);
	}

	/**
	 * Test findByFranchiseId
	 */
	// Test case for finding branchs by franchiseId when some exist, expecting a 200 OK and array of branch
	@Test
	void findByFranchiseId_shouldReturn200() {
		Branch item1 = new Branch("123xyz", "Sede Norte", franchiseId);
		Branch item2 = new Branch("123abc", "Sede Sur", franchiseId);

		when(useCase.getByFranchiseId(franchiseId)).thenReturn(Flux.just(item1, item2));

		client.get().uri("/franchises/{franchiseId}/branches", franchiseId)//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(2)//
				.jsonPath("$[0].id").isEqualTo("123xyz")//
				.jsonPath("$[1].id").isEqualTo("123abc");
	}

	// Test case for finding branchs by franchiseId when none exist, expecting a 200 OK and an empty array
	@Test
	void findByFranchiseId_whenNoBranchsExist_shouldReturn200() {
		when(useCase.getByFranchiseId(franchiseId)).thenReturn(Flux.empty());

		client.get().uri("/franchises/{franchiseId}/branches", franchiseId)//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(0);
	}
}