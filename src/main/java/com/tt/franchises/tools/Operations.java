package com.tt.franchises.tools;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.google.gson.Gson;

/**
 * Utility class for common operations
 */
public class Operations {

	/**
	 * Converts an object to a JSON string
	 *
	 * @param object
	 * @return JSON string
	 */
	public static String convertObjectToJsonString(Object object) {

		Gson gson = new Gson();

		return gson.toJson(object);
	}

	/**
	 * Validates if a string is null or blank
	 *
	 * @param value
	 * @return true if the string is null or blank, false otherwise
	 */
	public static boolean validateString(String value) {
		return value == null || value.isBlank();
	}
	
	/**
	 * Validates if an integer is null or negative
	 *
	 * @param value
	 * @return true if the integer is null or negative, false otherwise
	 */
	public static boolean validateInteger(Integer value) {
		return value == null && !validateIntegerNegative(value);
	}
	
	/**
	 * Validates if an integer is not null or positive
	 *
	 * @param value
	 * @return true if the integer is not null and positive, false otherwise
	 */	
	public static boolean validateIntegerNegative(Integer value) {
		return value != null && value < 0;
	}
	
	/**
	 * Retrieves a localized message from the MessageSource
	 *
	 * @param messageSource the MessageSource to retrieve the message from
	 * @param key           the key of the message to retrieve
	 * @return the localized message corresponding to the provided key
	 */
	public static String getMessage(MessageSource messageSource, String key) {
	    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}
}
