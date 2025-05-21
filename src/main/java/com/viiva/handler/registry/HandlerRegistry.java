package com.viiva.handler.registry;

import java.util.HashMap;
import java.util.Map;
import com.viiva.exceptions.HandlerNotFoundException;
import com.viiva.handler.Handler;

public class HandlerRegistry {

	private static final Map<String, Class<? extends Handler>> handlerRegistry = new HashMap<>();

	static {
		registerHandler("com.viiva.handler.account.AccountHandler", com.viiva.handler.account.AccountHandler.class);
		registerHandler("com.viiva.handler.branch.BranchHandler", com.viiva.handler.branch.BranchHandler.class);
		registerHandler("com.viiva.handler.notification.NotificationHandler", com.viiva.handler.notification.NotificationHandler.class);
		registerHandler("com.viiva.handler.request.RequestHandler", com.viiva.handler.request.RequestHandler.class);
		registerHandler("com.viiva.handler.session.SessionHandler", com.viiva.handler.session.SessionHandler.class);
		registerHandler("com.viiva.handler.signin.SigninHandler", com.viiva.handler.signin.SigninHandler.class);
		registerHandler("com.viiva.handler.signup.SignupHandler", com.viiva.handler.signup.SignupHandler.class);
		registerHandler("com.viiva.handler.transaction.TransactionHandler", com.viiva.handler.transaction.TransactionHandler.class);
		registerHandler("com.viiva.handler.user.UserHandler", com.viiva.handler.user.UserHandler.class);
		registerHandler("com.viiva.handler.employee.EmployeeHandler",com.viiva.handler.employee.EmployeeHandler.class);
	}

	private static void registerHandler(String name, Class<? extends Handler> handlerClass) {
		handlerRegistry.put(name, handlerClass);
	}

	public static Handler getHandler(String name) throws Exception {
		Class<? extends Handler> handlerClass = handlerRegistry.get(name);

		if (handlerClass == null) {
			throw new HandlerNotFoundException("Handler not found for: " + name);
		}

		try {
			return handlerClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new HandlerNotFoundException("Failed to instantiate handler: " + name, e);
		}
	}

	public static boolean contains(String name) {
		return handlerRegistry.containsKey(name);
	}
}
