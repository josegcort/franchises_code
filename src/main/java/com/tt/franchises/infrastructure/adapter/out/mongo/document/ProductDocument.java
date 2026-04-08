package com.tt.franchises.infrastructure.adapter.out.mongo.document;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Document class representing a product in the MongoDB database.
 */
@Data
@AllArgsConstructor
@Document(collection = "products")
public class ProductDocument {
	private String id;
	private String name;
	private Integer stock;
}
