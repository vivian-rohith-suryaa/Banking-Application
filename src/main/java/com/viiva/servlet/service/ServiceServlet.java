package com.viiva.servlet.service;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.viiva.exceptions.AuthException;
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
		
		if (request.getRequestURI().endsWith("/auth/logout") && methodAction.equalsIgnoreCase("POST")) {
			handleLogout(request, response);
			return;
		}
		
		if (BasicUtil.isBlank(handlerClassName)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No handler found. (Null/Empty handler)");
			return;
		}

		try (BufferedReader reader = request.getReader()) {
			Handler handler = HandlerRegistry.getHandler(handlerClassName);
			Class<?> requestClass = handler.getRequestType();
			Object requestData = gson.fromJson(reader, requestClass);
			
			BufferedReader Xreader = request.getReader();
			String reqBody = Xreader.lines().collect(Collectors.joining());
			System.out.println("Raw Request Body: " + reqBody);
			
			System.out.println("Deserialized request:" + gson.toJson(requestData));

			if (BasicUtil.isNull(requestData)) {
				requestData = requestClass.getDeclaredConstructor().newInstance();
			}

			Map<String, Object> pathParams = ServletUtil.extractPathParams(request);
			
			Map<String, String[]> queryParamRaw = request.getParameterMap();
            Map<String, String> queryParams = new HashMap<>();
            for (Map.Entry<String, String[]> entry : queryParamRaw.entrySet()) {
                queryParams.put(entry.getKey(), entry.getValue()[0]);
                System.out.println(queryParams);
            }
            
			if (requestData instanceof UserWrapper) {
				((UserWrapper) requestData).setPathParams(pathParams);
				((UserWrapper) requestData).setQueryParams(queryParams);
			} else if (requestData instanceof Branch) {
				((Branch) requestData).setPathParams(pathParams);
				((Branch) requestData).setQueryParams(queryParams);
			} else if (requestData instanceof Employee) {
				((Employee) requestData).setPathParams(pathParams);
				((Employee) requestData).setQueryParams(queryParams);
			} else if (requestData instanceof Request) {
				((Request) requestData).setPathParams(pathParams);
				((Request) requestData).setQueryParams(queryParams);
			} else if (requestData instanceof AccountRequest) {
				((AccountRequest) requestData).setPathParams(pathParams);
				((AccountRequest) requestData).setQueryParams(queryParams);
			} else if (requestData instanceof AccountTransaction) {
				((AccountTransaction) requestData).setPathParams(pathParams);
				((AccountTransaction) requestData).setQueryParams(queryParams);
			}
			
			if (requestData instanceof SessionAware) {
			    HttpSession session = request.getSession(false);
			    if (session == null) {
			        throw new AuthException("Session not found.");
			    }

			    Map<String, Object> sessionMap = new HashMap<>();
			    sessionMap.put("userId", session.getAttribute("userId"));
			    sessionMap.put("role", session.getAttribute("role"));
			    sessionMap.put("branchId", session.getAttribute("branchId"));
			    ((SessionAware) requestData).setSessionAttributes(sessionMap);
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
	
	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		Cookie cookie = new Cookie("JSESSIONID", "");
		cookie.setMaxAge(0);
		cookie.setPath(request.getContextPath()); 
		cookie.setHttpOnly(true);
		cookie.setSecure(true); 
		response.addCookie(cookie);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("{\"message\": \"Logged out successfully\"}");
	}


}
