package com.jexbox;

import java.io.UnsupportedEncodingException;

import com.google.gson.JsonObject;

public interface Notifier {
	public void send(JsonObject json) throws TransportException, UnsupportedEncodingException;
}
