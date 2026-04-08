package com.tt.franchises.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for franchise creation requests.
 */
@Data
@AllArgsConstructor
public class FranchiseRequest {

	@NotBlank(message = "error.franchise.name.required")
	private String name;
}
