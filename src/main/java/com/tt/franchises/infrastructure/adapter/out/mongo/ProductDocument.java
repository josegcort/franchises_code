package com.tt.franchises.infrastructure.adapter.out.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(collection = "products")
public class ProductDocument {
	private String id;
	private String name;
	private Integer stock;
}
