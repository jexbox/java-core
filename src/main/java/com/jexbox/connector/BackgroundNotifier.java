package com.jexbox.connector;

import java.io.UnsupportedEncodingException;

import com.google.gson.JsonObject;

public class BackgroundNotifier implements Notifier{

	private MemoryQueue _queue;
	private Transport _transport;
	
	public BackgroundNotifier(){
		super();
		_queue = new MemoryQueue();
		_transport = new Transport(_queue);
		_transport.start();
	}
	
	public void send(JsonObject json) throws TransportException, UnsupportedEncodingException {
		_queue.add(json);
	}
}
