package com.viiva.servlet.service;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.HandlerNotFoundException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.handler.registry.HandlerRegistry;
import com.viiva.pojo.branch.Branch;
import com.viiva.pojo.employee.Employee;
import com.viiva.pojo.request.Request;
import com.viiva.session.SessionAware;
import com.viiva.util.BasicUtil;
import com.viiva.util.ResponseUtil;
import com.viiva.util.ServletUtil;
import com.viiva.wrapper.account.AccountRequest;
import com.viiva.wrapper.account.AccountTransaction;
import com.viiva.wrapper.user.UserWrapper;
import com.viiva.util.SessionUtil;


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

		System.out.println("\nIncoming request to server.");
		String handlerClassName = (String) request.getAttribute("handler");
		String methodAction = request.getMethod();

		System.out.println(request.getRequestURI());

		System.out.println("Handler: " + handlerClassName + "\nMethod: " + methodAction);

		if (BasicUtil.isBlank(handlerClassName)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No handler found. (Null/Empty handler)");
			return;
		}

		try (BufferedReader reader = request.getReader()) {
			Handler handler = HandlerRegistry.getHandler(handlerClassName);
			Class<?> requestClass = handler.getRequestType();
			Object requestData = gson.fromJson(reader, requestClass);
			
			System.out.println("Deserialized request:" + gson.toJson(requestData));

			if (BasicUtil.isNull(requestData)) {
				requestData = requestClass.getDeclaredConstructor().newInstance();
			}

			Map<String, Object> pathParams = ServletUtil.extractPathParams(request);
			
			if (requestData instanceof UserWrapper) {
				((UserWrapper) requestData).setPathParams(pathParams);
			} else if (requestData instanceof Branch) {
				((Branch) requestData).setPathParams(pathParams);
			} else if (requestData instanceof Employee) {
				((Employee) requestData).setPathParams(pathParams);
			} else if (requestData instanceof Request) {
				((Request) requestData).setPathParams(pathParams);
			} else if (requestData instanceof AccountRequest) {
				((AccountRequest) requestData).setPathParams(pathParams);
			} else if (requestData instanceof AccountTransaction) {
				((AccountTransaction) requestData).setPathParams(pathParams);
			}
			
			if (requestData instanceof SessionAware) {
			    if (request.getSession(false) != null) {
			        Map<String, Object> sessionAttributes = SessionUtil.extractSessionAttributes(request);
			        ((SessionAware) requestData).setSessionAttributes(sessionAttributes);
			    }
			}

					
			Object result = handler.handle(methodAction, requestData);

			Map<String, Object> resultData = (Map<String, Object>) result;
			
			if (request.getRequestURI().endsWith("/auth/signin") && methodAction.equalsIgnoreCase("POST")) {
			    SessionUtil.setupUserSession(request, resultData);
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			System.out.println(resultData);
			ResponseUtil.sendSuccess(response, resultData);

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
