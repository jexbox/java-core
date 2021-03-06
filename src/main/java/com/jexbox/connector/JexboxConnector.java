package com.jexbox.connector;

import java.util.Map;

import com.google.gson.JsonObject;

public interface JexboxConnector {
	public void sendJson(JsonObject json);
	public void send(Throwable e);
	public void sendWithMeta(Throwable e, Map<String, Map<String, String>> metaD);
	public JsonObject json(Throwable e);
	public JsonObject json(Throwable e, Map<String, Map<String, String>> metaD);

	public String getVersion();
	public String getUrl();
	public boolean isSsl();
	public String getHost();
	public String getEnv();
	
}
