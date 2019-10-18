package com.springSecurity.helper;

import java.util.LinkedHashMap;

/**
 * To create json response.
 * 
 * @author 
 *
 * @param <T>
 */
public class JsonObjectHelper extends LinkedHashMap<String, Object> {
	public JsonObjectHelper(boolean error, String message, Object object) {
		this.put("error", error);
		this.put("message", message);
		this.put("data", object);
	}

	public JsonObjectHelper(boolean error, String message) {
		this.put("error", error);
		this.put("message", message);
	}

	public void putData(Object object) {
		this.put("data", object);
	}
}
