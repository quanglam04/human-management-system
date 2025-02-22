package com.vti.lab7.exception.custom;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

	private Object[] params;

	public BadRequestException(String message, Object... params) {
		super(message);
		this.params = params;
	}
}
