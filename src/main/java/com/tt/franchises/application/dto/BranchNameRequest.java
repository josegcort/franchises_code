package com.tt.franchises.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for branch creation requests.
 */
@Data
@AllArgsConstructor
public class BranchNameRequest {

	@NotBlank(message = "error.branch.name.required")
	private String name;

}
