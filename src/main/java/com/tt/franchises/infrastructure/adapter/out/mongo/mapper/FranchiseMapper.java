package com.tt.franchises.infrastructure.adapter.out.mongo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.domain.model.Franchise;
import com.tt.franchises.infrastructure.adapter.out.mongo.document.BranchDocument;
import com.tt.franchises.infrastructure.adapter.out.mongo.document.FranchiseDocument;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FranchiseMapper {

	private final BranchMapper branchMapper;

	// Document > Domain
	public Franchise toDomain(FranchiseDocument doc) {
		if (doc == null)
			return null;

		return new Franchise(doc.getId(), doc.getName());
	}

	// Domain > Document
	public FranchiseDocument toDocument(Franchise model) {
		if (model == null)
			return null;

		return new FranchiseDocument(model.getId(), model.getName());
	}

}
