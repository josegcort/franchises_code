package com.tt.franchises.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model for a product available in a branch.
 */
@Data
@AllArgsConstructor
public class Product {
	private String id;
	private String name;
	private Integer stock;
	private String branchId;
}
