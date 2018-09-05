package org.rainbow.solar.dto;

import java.io.Serializable;

public class SolarError implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2481493136586447127L;
	
	private int code;
	private String message;

	public SolarError() {
		super();
	}

	public SolarError(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
