package com.tt.franchises.tools;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.google.gson.Gson;

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

	public static boolean validateString(String value) {
		return value == null || value.isBlank();
	}
	
	public static String getMessage(MessageSource messageSource, String key) {
	    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}
}
