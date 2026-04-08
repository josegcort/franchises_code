package com.tt.franchises.infrastructure.adapter.out.mongo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tt.franchises.domain.model.Branch;
import com.tt.franchises.domain.model.Product;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BranchMapper {

	private final ProductMapper productMapper;

	// Document > Domain
	public Branch toDomain(BranchDocument doc) {
		if (doc == null)
			return null;

		List<Product> products = doc.getProducts() == null //
				? List.of()//
				: doc.getProducts().stream()//
						.map(item -> productMapper.toDomain(item))//
						.collect(Collectors.toList());

		return new Branch(doc.getId(), doc.getName(), products);
	}

	// Domain > Document
	public BranchDocument toDocument(Branch model) {
		if (model == null)
			return null;

		List<ProductDocument> products = model.getProducts() == null //
				? List.of()//
				: model.getProducts().stream()//
						.map(item -> productMapper.toDocument(item))//
						.collect(Collectors.toList());

		return new BranchDocument(model.getId(), model.getName(), products);
	}

}
