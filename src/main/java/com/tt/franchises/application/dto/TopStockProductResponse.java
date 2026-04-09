package com.tt.franchises.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for product top stock response.
 */
@Data
@AllArgsConstructor
public class TopStockProductResponse {
	private String branchId;
	private String branchName;
	private String productId;
	private String productName;
	private int stock;
}
