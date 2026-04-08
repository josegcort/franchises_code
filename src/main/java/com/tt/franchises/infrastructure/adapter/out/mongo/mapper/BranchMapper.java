package com.tt.franchises.infrastructure.adapter.out.mongo.mapper;

import org.springframework.stereotype.Component;

import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.infrastructure.adapter.out.mongo.document.BranchDocument;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BranchMapper {

	// Document > Domain
	public Branch toDomain(BranchDocument doc) {
		if (doc == null)
			return null;

		return new Branch(doc.getId(), doc.getName(), doc.getFranchiseId());
	}

	// Domain > Document
	public BranchDocument toDocument(Branch model) {
		if (model == null)
			return null;

		return new BranchDocument(model.getId(), model.getName(), model.getFranchiseId());
	}

}
