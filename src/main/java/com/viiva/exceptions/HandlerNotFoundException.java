package com.viiva.exceptions;

public class HandlerNotFoundException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public HandlerNotFoundException(String message) {
		super(message);
	}
	
	public HandlerNotFoundException(String message, Throwable cause) {
		super(message,cause);
	}
}
