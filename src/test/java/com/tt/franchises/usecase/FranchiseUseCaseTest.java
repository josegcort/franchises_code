package com.tt.franchises.usecase;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.application.usecase.FranchiseUseCase;
import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.domain.port.FranchiseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit Tests for FranchiseUseCase
 */
@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

	@Mock
	private FranchiseRepository repository;

	@Mock
	private MessageSource msgSrc;

	@InjectMocks
	private FranchiseUseCase useCase;

	/**
	 * Test Create
	 */
	// Test to validate the correct creation of a franchise
	@Test
	void create_shouldSaveAndReturnFranchise() {
		Franchise itemNew = new Franchise(null, "Sede AXM", List.of());
		Franchise itemSaved = new Franchise("123xyz", "Sede AXM", List.of());

		when(repository.findByNameIgnoreCase("Sede AXM")).thenReturn(Mono.empty());
		when(repository.save(any(Franchise.class))).thenReturn(Mono.just(itemSaved));

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectNextMatches(//
						f -> f.getId().equals("123xyz") && f.getName().equals("Sede AXM"))//
				.verifyComplete();
	}

	// Test to validate that creating a franchise with an existing name returns a
	// conflict error
	@Test
	void create_whenItAlreadyExists_shouldReturnConflict() {
		Franchise itemOld = new Franchise("123xyz", "Sede AXM", List.of());
		Franchise itemNew = new Franchise(null, "Sede AXM", List.of());

		when(repository.findByNameIgnoreCase("Sede AXM")).thenReturn(Mono.just(itemOld));

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.CONFLICT)//
				.verify();
	}

	// Test to validate that creating a franchise with a null or empty name returns
	// a bad request error
	@Test
	void create_whenNameIsNull_shouldReturnBadRequest() {
		Franchise itemNew = new Franchise(null, null, List.of());

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)//
				.verify();
	}

	/**
	 * Test FindById
	 */
	// Test to validate that searching for a non-existent franchise by ID returns a
	// not found error
	@Test
	void getById_whenItDoesNotExist_shouldReturnNotFound() {
		when(repository.findById(anyString())).thenReturn(Mono.empty());

		StepVerifier.create(//
				useCase.getById("123xyz"))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.NOT_FOUND //
				).verify();
	}

	// Test to validate that searching for an existing franchise by ID returns the
	// franchise
	@Test
	void findById_whenItExists_shouldReturnFranchise() {
		Franchise itemNew = new Franchise("123xyz", "Sede AXM", List.of());

		when(repository.findById("123xyz")).thenReturn(Mono.just(itemNew));

		StepVerifier.create(//
				useCase.getById("123xyz"))//
				.expectNextMatches(//
						f -> f.getId().equals("123xyz"))//
				.verifyComplete();
	}

	/**
	 * Test FindAll
	 */
	// Test to validate that searching for all franchises returns the correct list
	// of franchises
	@Test
	void findAll_shouldReturnAllFranchises() {
		Franchise item1 = new Franchise("123xyz", "Sede AXM", List.of());
		Franchise item2 = new Franchise("123abc", "Sede PEI", List.of());

		when(repository.findAll()).thenReturn(Flux.just(item1, item2));

		StepVerifier.create(//
				useCase.getAll())//
				.expectNextMatches(//
						f -> f.getName().equals("Sede AXM")//
				).expectNextMatches(//
						f -> f.getName().equals("Sede PEI"))//
				.verifyComplete();
	}

	// Test to validate that searching for all franchises returns an empty list when
	// no franchises exist
	@Test
	void findAll_whenNoFranchisesExist_shouldReturnEmptyFlux() {
		when(repository.findAll()).thenReturn(Flux.empty());

		StepVerifier.create(//
				useCase.getAll())//
				.expectNextCount(0)//
				.verifyComplete();
	}

}