package com.compprog1282025.service;

public class InvalidAccessException extends RuntimeException {

	public InvalidAccessException() {};
	
	public InvalidAccessException(String errorMessage) {
		super(errorMessage);
	}
	
	public InvalidAccessException(Throwable cause) {
		super(cause);
	}
	
	public InvalidAccessException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

}
