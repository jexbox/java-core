package com.jexbox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Transport implements Runnable{
    private static Logger _logger = Logger.getLogger(Transport.class.getName());

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
    		_logger.log(Level.INFO, "You can only start idle workers");
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
					_logger.log(Level.SEVERE, e.getMessage(), e);
				}
				_queue.remove();				
			}
		}
	}

    public static void send(JsonObject json) throws TransportException, UnsupportedEncodingException{
    	String appId = json.get("appId").getAsString();
    	
    	JsonElement jh = json.remove("host");
    	String host = jh != null ? jh.getAsString() : null;
    	
    	JsonElement jph = json.remove("proxyHost");
    	String proxyHost = jph != null ? jph.getAsString() : null;
    	
    	JsonElement jp = json.remove("proxyPort");
    	int port = jp != null ? jp.getAsInt() : null;
    	
    	JsonElement jusp = json.remove("useSystemProxy");
    	boolean useSystemProxy = jusp != null ? jusp.getAsBoolean() : null;
    	
    	send(host, proxyHost, port, useSystemProxy, json.toString(), "application/json", appId);
    }

    public static void send(String host, String proxyHost, int port, boolean useSystemProxy, JsonObject json, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, proxyHost, port, useSystemProxy, json.toString(), "application/json", appId);
    }

    public static void send(String host, String proxyHost, int port, boolean useSystemProxy, InputStream is, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, proxyHost, port, useSystemProxy, is, "application/json", appId);
    }

    public static void send(String host, String proxyHost, int port, boolean useSystemProxy, String data, String ct, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, proxyHost, port, useSystemProxy, new ByteArrayInputStream(data.getBytes("UTF-8")), ct, appId);
    }

    public static void send(String host, String proxyHost, int port, boolean useSystemProxy, InputStream data, String ct, String appId) throws TransportException{
        HttpURLConnection conn = null;
        try {
            URL url = new URL(host);
            if(proxyHost != null && proxyHost.length() > 0){
            	SocketAddress addr = new InetSocketAddress(proxyHost, port);
            	Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
                conn = (HttpURLConnection) url.openConnection(proxy);
            }else{
            	if(useSystemProxy){
            		Proxy proxy = getSystemProxy(url.toURI());
            		if(proxy != null){
                        conn = (HttpURLConnection) url.openConnection(proxy);
            		}else{
                        conn = (HttpURLConnection) url.openConnection();
            		}
            	}else{
                    conn = (HttpURLConnection) url.openConnection();
            	}
            }
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
        } catch (URISyntaxException e) {
            throw new TransportException(String.format("Connection error while sending data to %s", host), e);
		} finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

	public static Proxy getSystemProxy(URI host){
		System.setProperty("java.net.useSystemProxies","true");
        List<Proxy> list = ProxySelector.getDefault().select(host);
        Proxy proxy = null;
        if(list != null && list.size() > 0){
        	proxy = list.get(0);
        }
        return proxy;
	}

}
