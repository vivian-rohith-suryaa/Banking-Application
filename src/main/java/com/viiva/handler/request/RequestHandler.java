package com.viiva.handler.request;

import com.viiva.handler.Handler;
import com.viiva.wrapper.account.AccountRequest;

public class RequestHandler implements Handler<AccountRequest> {

	@Override
	public Object handle(String methodAction, AccountRequest requestData) throws Exception {
		return null;
	}

	@Override
	public Class<AccountRequest> getRequestType() {
		return AccountRequest.class;
	}


}
