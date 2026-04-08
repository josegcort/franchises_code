package com.tt.franchises.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
	private String id;
	private String name;
	private Integer stock;
}
