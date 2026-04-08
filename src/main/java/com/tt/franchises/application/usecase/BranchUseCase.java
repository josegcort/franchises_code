package com.tt.franchises.application.usecase;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.domain.port.BranchRepository;
import com.tt.franchises.domain.port.FranchiseRepository;
import com.tt.franchises.tools.Operations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use case for managing branches.
 */
@Slf4j
@Component
@AllArgsConstructor
public class BranchUseCase {

	private final BranchRepository branchRepo;
	private final FranchiseRepository franchiseRepo;
	private final MessageSource msgSrc;

	// Create a new branch
	public Mono<Branch> create(Branch branch) {

		// Validate that the franchiseId is not null or empty
		if (Operations.validateString(branch.getFranchiseId())) {
			String error = Operations.getMessage(msgSrc, "error.branch.franchiseId.required");

			log.error(error);

			return Mono.error(new ResponseStatusException(//
					HttpStatus.BAD_REQUEST, error//
			));
		}

		// Validate that the name is not null or empty
		if (Operations.validateString(branch.getName())) {
			String error = Operations.getMessage(msgSrc, "error.branch.name.required");

			log.error(error);

			return Mono.error(new ResponseStatusException(//
					HttpStatus.BAD_REQUEST, error//
			));
		}

		return franchiseRepo.findById(branch.getFranchiseId())
				// Validate that the franchise exists
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						Operations.getMessage(msgSrc, "error.franchise.notFoundById"))))

				// Validate that the name is unique
				.flatMap(franchise -> //
				branchRepo.findByNameIgnoreCaseAndFranchiseId(branch.getName(), branch.getFranchiseId())//
						.hasElement()//
						.flatMap(exists -> {//
							if (exists) {
								return Mono.error(new ResponseStatusException(//
										HttpStatus.CONFLICT, //
										Operations.getMessage(msgSrc, "error.branch.name.duplicate")//
								));
							}
							Branch toSave = new Branch(null, branch.getName(), branch.getFranchiseId());
							return branchRepo.save(toSave);
						}));
	}

	// Get a branch by ID
	public Mono<Branch> getById(String id) {
		return branchRepo.findById(id).switchIfEmpty(//
				Mono.error(//
						new ResponseStatusException(//
								HttpStatus.NOT_FOUND, Operations.getMessage(msgSrc, "error.branch.notFoundById")//
						)//
				));
	}

	// Get all branchs
	public Flux<Branch> getAll() {
		return branchRepo.findAll();
	}

	// Get a branch by franchiseId
	public Flux<Branch> getByFranchiseId(String franchiseId) {
		return branchRepo.findByFranchiseId(franchiseId);
	}

}
