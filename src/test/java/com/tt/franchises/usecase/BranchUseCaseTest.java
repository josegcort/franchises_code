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

import com.tt.franchises.application.usecase.BranchUseCase;
import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.domain.port.BranchRepository;
import com.tt.franchises.domain.port.FranchiseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit Tests for BranchUseCase
 */
@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

	@Mock
	private BranchRepository branchRepo;

	@Mock
	private FranchiseRepository franchiseRepo;

	@Mock
	private MessageSource msgSrc;

	@InjectMocks
	private BranchUseCase useCase;

	// variable global franchise franchiseId and franchise for tests
	private final String franchiseId = "321abc";
	private final Franchise franchise = new Franchise(franchiseId, "AXM");

	/**
	 * Test Create
	 */
	// Test to validate the correct creation of a branch
	@Test
	void create_shouldSaveAndReturnBranch() {
		Branch itemNew = new Branch(null, "Sede Norte", franchiseId);
		Branch itemSaved = new Branch("123xyz", "Sede Norte", franchiseId);

		when(franchiseRepo.findById(franchiseId)).thenReturn(Mono.just(franchise));
		when(branchRepo.findByNameIgnoreCaseAndFranchiseId("Sede Norte", franchiseId)).thenReturn(Mono.empty());
		when(branchRepo.save(any(Branch.class))).thenReturn(Mono.just(itemSaved));

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectNextMatches(//
						b -> b.getId().equals("123xyz")//
								&& b.getName().equals("Sede Norte"))//
				.verifyComplete();
	}

	// Test to validate that creating a branch with a null or empty name returns
	// a bad request error
	@Test
	void create_whenNameIsEmpty_shouldReturnBadRequest() {
		Branch itemNew = new Branch(null, null, franchiseId);

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)//
				.verify();
	}

	// Test case for creating a branch with an null or empty name, expecting a Bad
	// Request response
	@Test
	void create_whenFranchiseIsNEmpty_shouldReturnBadRequest() {
		Branch itemNew = new Branch(null, "Sede Norte", "");

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.BAD_REQUEST)
				.verify();
	}

	// Test to validate that creating a branch for a non-existent franchise returns
	// a not found error
	@Test
	void create_whenFranchiseDoesNotExist_shouldReturnNotFound() {
		Branch itemNew = new Branch(null, "Sede Norte", franchiseId);

		when(franchiseRepo.findById(franchiseId)).thenReturn(Mono.empty());

		StepVerifier.create(//
				useCase.create(itemNew))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.NOT_FOUND)
				.verify();
	}

	// Test to validate that creating a branch with an existing name returns a
	// conflict error
	@Test
	void create_whenItAlreadyExists_shouldReturnConflict() {
		Branch itemOld = new Branch("123xyz", "Sede Norte", franchiseId);
		Branch itemNew = new Branch(null, "Sede Norte", franchiseId);

		when(franchiseRepo.findById(franchiseId)).thenReturn(Mono.just(franchise));
		when(branchRepo.findByNameIgnoreCaseAndFranchiseId("Sede Norte", franchiseId)).thenReturn(Mono.just(itemOld));

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
	// Test to validate that searching for a non-existent branch by ID returns a
	// not found error
	@Test
	void getById_whenItDoesNotExist_shouldReturnNotFound() {
		when(branchRepo.findById(anyString())).thenReturn(Mono.empty());

		StepVerifier.create(//
				useCase.getById("123xyz"))//
				.expectErrorMatches(//
						ex -> ex instanceof ResponseStatusException rse //
								&& rse.getStatusCode() == HttpStatus.NOT_FOUND //
				).verify();
	}

	// Test to validate that searching for an existing branch by ID returns the
	// branch
	@Test
	void findById_whenItExists_shouldReturnBranch() {
		Branch itemSaved = new Branch("123xyz", "Sede Norte", franchiseId);

		when(branchRepo.findById("123xyz")).thenReturn(Mono.just(itemSaved));

		StepVerifier.create(//
				useCase.getById("123xyz"))//
				.expectNextMatches(//
						b -> b.getId().equals("123xyz"))//
				.verifyComplete();
	}

	/**
	 * Test FindAll
	 */
	// Test to validate that searching for all branchs returns the correct list
	// of branchs
	@Test
	void findAll_shouldReturnAllBranches() {
		Branch item1 = new Branch("123xyz", "Sede Norte", franchiseId);
		Branch item2 = new Branch("123abc", "Sede Sur", franchiseId);

		when(branchRepo.findAll()).thenReturn(Flux.just(item1, item2));

		StepVerifier.create(//
				useCase.getAll())//
				.expectNextMatches(//
						b -> b.getName().equals("Sede Norte")//
				).expectNextMatches(//
						b -> b.getName().equals("Sede Sur"))//
				.verifyComplete();
	}

	// Test to validate that searching for all branchs returns an empty list when
	// no branchs exist
	@Test
	void findAll_whenNoBranchsExist_shouldReturnEmptyFlux() {
		when(branchRepo.findAll()).thenReturn(Flux.empty());

		StepVerifier.create(//
				useCase.getAll())//
				.expectNextCount(0)//
				.verifyComplete();
	}

	/**
	 * Test FindByFranchiseId
	 */
	// Test to validate that searching for branchs by franchise ID returns the
	// correct list of branchs for that franchise
	@Test
	void findByFranchiseId_shouldReturnByFranchiseIdBranches() {
		Branch item1 = new Branch("123xyz", "Sede Norte", franchiseId);
		Branch item2 = new Branch("123abc", "Sede Sur", franchiseId);

		when(branchRepo.findByFranchiseId(franchiseId)).thenReturn(Flux.just(item1, item2));

		StepVerifier.create(//
				useCase.getByFranchiseId(franchiseId))//
				.expectNextMatches(//
						b -> b.getName().equals("Sede Norte")//
				).expectNextMatches(//
						b -> b.getName().equals("Sede Sur"))//
				.verifyComplete();
	}

	// Test to validate that searching for branchs by franchise ID returns an empty
	// list when no branchs exist for that franchise
	@Test
	void findByFranchiseId_whenNoBranchsExist_shouldReturnEmptyFlux() {
		when(branchRepo.findByFranchiseId(franchiseId)).thenReturn(Flux.empty());

		StepVerifier.create(//
				useCase.getByFranchiseId(franchiseId))//
				.expectNextCount(0)//
				.verifyComplete();
	}

}