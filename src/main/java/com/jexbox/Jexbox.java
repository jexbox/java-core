package com.jexbox;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Jexbox {
    private static Log log = LogFactory.getLog(Jexbox.class);

    private static final String NOTIFIER_URL = "https://jexbox.com/java";
    private static final String NOTIFIER_NAME = "Java Jexbox Notifier";
    private static final String NOTIFIER_VERSION = "0.0.1";

    protected static final String DEFAULT_HOST = "notify.jexbox.com";

    private String name = NOTIFIER_NAME;
	private String version = NOTIFIER_VERSION;
    private String url = NOTIFIER_URL;

    private String appId;
    private boolean ssl = false;
    private String host = DEFAULT_HOST;

	public Jexbox(Properties props) {
		super();
		init(props);
	}
	
	private void init(Properties props){
		appId = (String) props.get("appId");
		if(props.containsKey("host")){
			host = (String) props.get("host");
		}
		if(props.containsKey("ssl")){
			ssl = Boolean.parseBoolean((String) props.get("ssl"));
		}
	}
	
	public String getAppId(){
		return appId;
	}
	
	public void send(Throwable e){
		JsonObject json = json(e);
		try {
			Transport.send(getHttpHost(), json, getAppId());
		} catch (UnsupportedEncodingException e1) {
			log.error("Could not able to send error to Jexbox", e1);
		} catch (TransportException e1) {
			log.error("Could not able to send error to Jexbox", e1);
		}
	}

	public JsonObject json(Throwable e){
		JsonObject json = new JsonObject();
		json.add("appId", new JsonPrimitive(getAppId()));

		JsonObject notifier = new JsonObject();
		json.add("notifier", notifier);
		notifier.add("name", new JsonPrimitive(getName()));
		notifier.add("version", new JsonPrimitive(getVersion()));
		notifier.add("url", new JsonPrimitive(getUrl()));
		
		JsonArray exceptions = new JsonArray();
		json.add("exceptions", exceptions);
		
		Throwable ex = e;
        while(ex != null) {
    		JsonObject jex = new JsonObject();
    		exceptions.add(jex);
    		jex.add("class", new JsonPrimitive(ex.getClass().getName()));
    		jex.add("message", new JsonPrimitive(ex.getMessage()));
    		
    		JsonArray stack = new JsonArray();
    		jex.add("stacktrace", stack);
    		
    		StackTraceElement[] stackTrace = ex.getStackTrace();
            for(int i=0; i < stackTrace.length; i++) {
            	StackTraceElement el = stackTrace[i];
            	stack.add(new JsonPrimitive(el.toString()));
            }
            
            ex = ex.getCause();
        }		
		return json;
	}
	
    public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getUrl() {
		return url;
	}

	public boolean isSsl() {
		return ssl;
	}

	public String getHost() {
		return host;
	}
	
    private String getHttpHost() {
        return (ssl ? "https://" : "http://") + getHost();
    }

	
/*
	public String json(Throwable e){
		StringBuffer json = new StringBuffer();
		json.append("{\n");//start object
		
		json.append("\"appId\"");//add property appId
		json.append(":");
		json.append("\""+getAppId()+"\"");//add appId value
		json.append(",\n");
		
		json.append("\"exceptions\"");//add array aproperty exceptions
		json.append(":[\n");
	
		Throwable ex = e;
        while(ex != null) {
    		json.append("{");//start exception class
    		
    		json.append("\"errorClass\"");//add property name
    		json.append(":");//add property div
    		json.append("\""+ex.getClass().getName()+"\"");//add property value
    		json.append(",\n");
    		
    		json.append("\"message\"");//add property name
    		json.append(":");//add property div
    		json.append("\""+ex.getMessage()+"\"");//add property value
    		json.append(",\n");
    		
    		json.append("\"stacktrace\"");//add array aproperty stacktrace
    		json.append(":[");
            
    		StackTraceElement[] stackTrace = ex.getStackTrace();
            for(int i=0; i < stackTrace.length; i++) {
            	StackTraceElement el = stackTrace[i];
        		json.append("\""+el.toString()+"\"");//add property value
        		if(i < stackTrace.length-1) json.append(",\n");
            }
            
    		json.append("]");//end array aproperty stacktrace
    		
    		json.append("}\n");//end exception class
            ex = ex.getCause();
            if(ex != null) json.append(",\n");
        }		
		
		json.append("]\n");//end array aproperty exceptions
		
		json.append("}");//end object
		
		return json.toString();
	}
*/
}
