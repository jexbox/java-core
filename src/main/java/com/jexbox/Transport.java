package com.jexbox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.JsonObject;

public class Transport implements Runnable{
    private static Log log = LogFactory.getLog(Transport.class);

	private MemoryQueue _queue = null;
	private String _state = "idle";
	
	public Transport(MemoryQueue queue){
		super();
		_queue = queue;
	}
	
	public synchronized void stop() {
    	_state = "stop";
    	_queue.notifyWorker();
    }
	
    public synchronized void start(){
    	if(!"idle".equals(_state)){
//    		throw new Exception("You can only start idle workers");
    		log.warn("You can only start idle workers");
    		return;
    	}
    	Thread t = new Thread(this);
	    t.setDaemon(true);
	    _state = "running";
		t.start();
    }
    
	public synchronized String getState() {
		return _state;
	}
	
	public void run() {
		while(true){
			if("stop".equalsIgnoreCase(_state))
				break;
			JsonObject json = _queue.peek();
			if(json == null){
				_queue.waitWorker();
			}else{
				try {
					send(json);
				} catch (Throwable e) {
					log.error(e);
				}
				_queue.remove();				
			}
		}
	}

    public static void send(JsonObject json) throws TransportException, UnsupportedEncodingException{
    	String appId = json.get("appId").getAsString();
    	String host = json.get("host").getAsString();
    	send(host, json.toString(), "application/json", appId);
    }

    public static void send(String host, JsonObject json, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, json.toString(), "application/json", appId);
    }

    public static void send(String host, InputStream is, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, is, "application/json", appId);
    }

    public static void send(String host, String data, String ct, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, new ByteArrayInputStream(data.getBytes("UTF-8")), ct, appId);
    }

    public static void send(String host, InputStream data, String ct, String appId) throws TransportException{
        HttpURLConnection conn = null;
        try {
            URL url = new URL(host);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true); 
            conn.setChunkedStreamingMode(0);
            if(ct != null) {
                conn.addRequestProperty("Content-Type", ct);
            }
            conn.addRequestProperty("Authorization", appId);

            OutputStream out = null;
            try {
                out = conn.getOutputStream();
            
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = data.read(buffer)) != -1)
                {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                if(out != null) {
                    out.close();
                }
            }

            int status = conn.getResponseCode();
            if(status / 100 != 2) {
                throw new TransportException(host);
            }
        } catch (IOException e) {
            throw new TransportException(String.format("Connection error while sending data to %s", host), e);
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

}
