package com.tt.franchises.infrastructure.adapter.out.mongo.adapter;

import org.springframework.stereotype.Component;

import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.domain.port.FranchiseRepository;
import com.tt.franchises.infrastructure.adapter.out.mongo.mapper.FranchiseMapper;
import com.tt.franchises.infrastructure.adapter.out.mongo.repository.FranchiseMongoRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class FranchiseMongoAdapter implements FranchiseRepository {

	private final FranchiseMongoRepository repo;
	private final FranchiseMapper mapper;

	@Override
	public Mono<Franchise> save(Franchise franchise) {
		return repo.save(mapper.toDocument(franchise)).map(mapper::toDomain);
	}

	@Override
	public Mono<Franchise> findById(String id) {
		return repo.findById(id).map(mapper::toDomain);
	}

	@Override
	public Flux<Franchise> findAll() {
		return repo.findAll().map(mapper::toDomain);
	}

	@Override
	public Mono<Franchise> findByNameIgnoreCase(String name) {
		return repo.findByNameIgnoreCase(name).map(mapper::toDomain);
	}

}
