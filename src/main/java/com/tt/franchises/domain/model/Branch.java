package com.tt.franchises.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Branch {
	private String id;
    private String name;
    private List<Product> products;
}