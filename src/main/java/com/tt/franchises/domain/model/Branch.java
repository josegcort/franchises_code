package com.tt.franchises.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model for a branch within a franchise, contains multiple products.
 */
@Data
@AllArgsConstructor
public class Branch {
	private String id;
    private String name;
    private String franchiseId;
}