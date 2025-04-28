package com.viiva.servlet.service;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.HandlerNotFoundException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.handler.registry.HandlerRegistry;
import com.viiva.util.BasicUtil;
import com.viiva.util.ResponseUtil;

public class ServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Gson gson = new Gson();

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (!"application/json".equalsIgnoreCase(request.getContentType())) {
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Only application/json supported");
			return;
		}

		System.out.println("Incoming request to server.");
		String handlerClassName = (String) request.getAttribute("handler");
		String methodAction = request.getMethod();

		if (BasicUtil.isBlank(handlerClassName)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"No handler mapped for this route. (Null/Empty handler)");
			return;
		}

		try (BufferedReader reader = request.getReader()) {
			Handler handler = HandlerRegistry.getHandler(handlerClassName);
			Class<?> requestClass = handler.getRequestType();

			Object requestData = gson.fromJson(reader, requestClass);

			Object result = handler.handle(methodAction, requestData);
			
			Map<String, Object> resultData = (Map<String, Object>) result;

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			ResponseUtil.sendSuccess(response, result);
			
			HttpSession session = request.getSession();
			
			session.setAttribute("userId", resultData.get("userId"));
			session.setAttribute("userType", resultData.get("userType"));

		} catch (HandlerNotFoundException e) {
			ResponseUtil.sendError(response, 404, "Not Found", e.getMessage());
		} catch (InputException e) {
			ResponseUtil.sendError(response, 400, "Bad Request", e.getMessage());
		} catch (DBException e) {
			ResponseUtil.sendError(response, 500, "Database Error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtil.sendError(response, 500, "Internal Server Error", e.getMessage());
		}
	}

}
