package com.viiva.session;

import java.util.Map;

public interface SessionAware {
	
	void setSessionAttributes(Map<String, Object> sessionAttributes);
	
	Map<String, Object> getSessionAttributes();

    default long getSessionUserId() {
        Object id = getSessionAttributes().get("userId");
        return id instanceof Number ? ((Number) id).longValue() : -1;
    }

    default byte getSessionRole() {
        Object role = getSessionAttributes().get("role");
        return role instanceof Number ? ((Number) role).byteValue() : -1;
    }

    default Long getSessionBranchId() {
        Object branch = getSessionAttributes().get("branchId");
        return branch instanceof Number ? ((Number) branch).longValue() : null;
    }

}
