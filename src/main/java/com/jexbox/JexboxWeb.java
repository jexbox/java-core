package com.jexbox;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface JexboxWeb extends Jexbox{
	public void send(Throwable e, HttpServletRequest request);
	public void sendWithMeta(Throwable e, HttpServletRequest request, Map<String, Map<String, String>> metaD);
}
