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

import com.tt.franchises.application.dto.FranchiseRequest;
import com.tt.franchises.application.usecase.FranchiseUseCase;
import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.infrastructure.adapter.in.web.handler.FranchiseHandler;
import com.tt.franchises.infrastructure.adapter.in.web.router.FranchiseRouter;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Unit Tests for FranchiseHandlerTest
 */
@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

	// WebTestClient to perform HTTP requests in tests
	private WebTestClient client;

	// Create a Validator instance for testing
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Mock
	private MessageSource msgSrc;

	@Mock
	private FranchiseUseCase useCase;

	// Set up the WebTestClient with the FranchiseHandler and FranchiseRouter
	@BeforeEach
	void setUp() {
		FranchiseHandler handler = new FranchiseHandler(useCase, validator, msgSrc);
		FranchiseRouter router = new FranchiseRouter();

		// Build the WebTestClient with the router function
		client = WebTestClient//
				.bindToRouterFunction(router.franchiseRoutes(handler))//
				.build();
	}

	/**
	 * Test Create
	 */
	// Test case for creating a franchise successfully
	@Test
	void create_shouldSave_shouldReturn201() {
		Franchise itemSaved = new Franchise("123xyz", "AXM");

		when(useCase.create(any(Franchise.class))).thenReturn(Mono.just(itemSaved));

		FranchiseRequest franchiseRequest = new FranchiseRequest("AXM");

		client.post()//
				.uri("/franchises")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(franchiseRequest)//
				.exchange()//
				.expectStatus().isCreated()//
				.expectBody()//
				.jsonPath("$.id").isEqualTo(itemSaved.getId())//
				.jsonPath("$.name").isEqualTo(itemSaved.getName());
	}

	// Test case for creating a franchise with an empty name, expecting a 400 Bad
	// Request response
	@Test
	void create_whenNameIsNull_shouldReturn400() {
		FranchiseRequest franchiseRequest = new FranchiseRequest("");

		client.post()//
				.uri("/franchises")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(franchiseRequest)//
				.exchange()//
				.expectStatus().isBadRequest();
	}

	// Test case for creating a franchise with a duplicate name, expecting a 409
	@Test
	void create_whenItAlreadyExists_shouldReturn409() {
		Franchise itemNew = new Franchise(null, "AXM");

		when(useCase.create(itemNew)).thenReturn(Mono.error(//
				new ResponseStatusException(//
						HttpStatus.CONFLICT, "Ya existe una franquicia con este nombre.")//
		));

		FranchiseRequest franchiseRequest = new FranchiseRequest("AXM");

		client.post().uri("/franchises")//
				.contentType(MediaType.APPLICATION_JSON)//
				.bodyValue(franchiseRequest)//
				.exchange()//
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);
	}

	/**
	 * Test FindById
	 */
	// Test case for finding a franchise by ID that does not exist, expecting a 404
	// Not Found response
	@Test
	void getById_whenItDoesNotExist_shouldReturn404() {
		when(useCase.getById("123xyz")).thenReturn(Mono.error(//
				new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró una franquicia con este ID.")//
		));

		client.get().uri("/franchises/123xyz")//
				.exchange()//
				.expectStatus().isNotFound();
	}

	// Test case for finding a franchise by ID that exists, expecting a 200 OK
	// response with the correct data
	@Test
	void getById_whenItExists_shouldReturn200() {
		Franchise itemSaved = new Franchise("123xyz", "AXM");

		when(useCase.getById("123xyz")).thenReturn(Mono.just(itemSaved));

		client.get().uri("/franchises/123xyz")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$.name").isEqualTo("AXM");
	}

	/**
	 * Test FindAll
	 */
	// Test case for finding all franchises when some exist, expecting a 200 OK and
	// array of franchises
	@Test
	void getAll_shouldReturn200() {
		Franchise item1 = new Franchise("123xyz", "AXM");
		Franchise item2 = new Franchise("123abc", "PEI");

		when(useCase.getAll()).thenReturn(Flux.just(item1, item2));

		client.get().uri("/franchises")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(2)//
				.jsonPath("$[0].id").isEqualTo("123xyz")//
				.jsonPath("$[1].id").isEqualTo("123abc");
	}

	// Test case for finding all franchises when none exist, expecting a 200 OK and
	// an empty array
	@Test
	void getAll_whenNoFranchisesExist_shouldReturn200() {
		when(useCase.getAll()).thenReturn(Flux.empty());

		client.get().uri("/franchises")//
				.exchange()//
				.expectStatus().isOk()//
				.expectBody()//
				.jsonPath("$").isArray()//
				.jsonPath("$.length()").isEqualTo(0);
	}
}