package com.tt.franchises.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for product creation requests.
 */
@Data
@AllArgsConstructor
public class ProductRequest {

	@NotBlank(message = "error.product.name.required")
	private String name;

	@NotBlank(message = "error.product.branchId.required")
	private String branchId;

	@NotNull(message = "error.product.stock.required")
	@Min(value = 0, message = "error.product.stock.positive")
	private Integer stock;

}
