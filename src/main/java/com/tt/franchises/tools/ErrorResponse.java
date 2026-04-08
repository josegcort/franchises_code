package com.tt.franchises.tools;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents an error response structure for API responses.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
	private String error;
	private String status;
	private String message;
	private String path;
	private String requestId;
	private String timestamp;
}
