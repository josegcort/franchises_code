package com.tt.franchises.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FranchiseRequest {

	@NotBlank(message = "El nombre de la franquicia es obligatorio.")
	private String name;
}
