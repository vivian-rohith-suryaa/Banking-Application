package com.viiva.servlet.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.viiva.handler.Handler;
import com.viiva.util.Utility;

public class ServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String handlerClassName = (String) request.getAttribute("handler");

		if (Utility.checkNull(handlerClassName) || Utility.checkEmpty(handlerClassName)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"No handler mapped for this route. (Null/Empty handler)");
			return;
		}

		try {
			Class<?> handlerClass = Class.forName(handlerClassName);
			Object handlerInstance = handlerClass.getDeclaredConstructor().newInstance();

			if (!(handlerInstance instanceof Handler)) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid Handler.");
				return;
			}

			Handler handler = (Handler) handlerInstance;
			handler.handle(request, response);
		} catch (ClassNotFoundException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Handler Not Found." + handlerClassName);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Handler instantiation failed: " + e.getMessage());
		}
	}
}
