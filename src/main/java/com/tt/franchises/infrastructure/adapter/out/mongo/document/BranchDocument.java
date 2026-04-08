package com.tt.franchises.infrastructure.adapter.out.mongo.document;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Document class representing a branch in the MongoDB database.
 */
@Data
@AllArgsConstructor
@Document(collection = "branches")
public class BranchDocument {
	private String id;
    private String name;
    private String franchiseId;
}