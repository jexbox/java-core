package com.jexbox;

import java.util.Queue;

import com.google.gson.JsonObject;

public abstract class CAbstractQueue implements Queue<JsonObject>{
	public abstract void waitWorker();	
	public abstract void notifyWorker();	
}
