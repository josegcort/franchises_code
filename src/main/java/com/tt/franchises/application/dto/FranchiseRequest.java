package com.tt.franchises.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FranchiseRequest {

	@NotBlank(message = "El nombre es obligatorio")
	private String name;
}
