package com.vti.lab7.exception.custom;

import lombok.Getter;

@Getter

public class ConflictException extends Exception {
	private Object [] params; 
	public ConflictException(String message, Object... object) {
		super(message);
		this.params = object;
	}

}
