package com.tt.franchises.infrastructure.adapter.out.mongo;

import org.springframework.stereotype.Component;

import com.tt.franchises.domain.model.Product;

@Component
public class ProductMapper {

	// Document > Domain
	public Product toDomain(ProductDocument doc) {
		if (doc == null)
			return null;
		
		return new Product(doc.getId(), doc.getName(), doc.getStock());
	}

	// Domain > Document
	public ProductDocument toDocument(Product model) {
		if (model == null)
			return null;

		return new ProductDocument(model.getId(), model.getName(), model.getStock());
	}

}
