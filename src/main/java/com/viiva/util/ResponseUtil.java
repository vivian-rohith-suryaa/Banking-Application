package com.viiva.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseUtil {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static void sendJson(HttpServletResponse response, Map<String, Object> body) throws IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		mapper.writeValue(response.getWriter(), body);
	}

	public static void sendSuccess(HttpServletResponse response, Object data) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("status", "success");
		responseBody.put("data", data);
		sendJson(response, responseBody);
	}

	public static void sendError(HttpServletResponse response, int code, String error, String message)
			throws IOException {
		response.setStatus(code);
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("error", error);
		responseBody.put("message", message);
		sendJson(response, responseBody);
	}

}
