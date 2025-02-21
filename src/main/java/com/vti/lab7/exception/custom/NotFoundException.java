package com.vti.lab7.exception.custom;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

	private Object[] params;

	public NotFoundException(String message, Object... params) {
		super(message);
		this.params = params;
	}
}
