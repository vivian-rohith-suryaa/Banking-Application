package com.viiva.handler;

public interface Handler<T> {

	public Object handle(String methodAction, T requestData) throws Exception;
	
	public Class<T> getRequestType();

}
