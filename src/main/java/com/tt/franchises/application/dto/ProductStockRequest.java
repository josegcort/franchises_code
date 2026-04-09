package com.tt.franchises.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for product update requests.
 */
@Data
@AllArgsConstructor
public class ProductStockRequest {

	@NotNull(message = "error.product.stock.required")
	@Min(value = 0, message = "error.product.stock.positive")
	private Integer stock;

}
