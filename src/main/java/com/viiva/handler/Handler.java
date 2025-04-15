package com.viiva.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Handler {

	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
