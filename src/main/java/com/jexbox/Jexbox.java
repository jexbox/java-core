package com.jexbox;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Jexbox implements Notifier{
    private static Logger _logger = Logger.getLogger(Jexbox.class.getName());

    public static final String NOTIFIER_URL = "https://jexbox.com/java";
    public static final String NOTIFIER_NAME = "Java Jexbox Notifier";
    public static final String NOTIFIER_VERSION = "0.0.1";
    public static final String ENVIRONMENT = "production";
    public static final String DEFAULT_HOST = "notify.jexbox.com";

    protected String name;
    protected String version;
    protected String url;
    protected String env;

    protected String appVersion;

    protected String appId;
    protected boolean ssl;
    protected String host;
    protected Notifier _notifier;
    
	public Jexbox(Properties props) {
		super();
		init(props);
	}
	
	private void init(Properties props){
	    name = NOTIFIER_NAME;
	    version = NOTIFIER_VERSION;
	    url = NOTIFIER_URL;
	    env = ENVIRONMENT;
	    appVersion = "0.0.1";
	    ssl = false;
	    host = DEFAULT_HOST;
	    _notifier = this;
		
		appId = (String) props.get("appId");
		if(props.containsKey("host")){
			host = (String) props.get("host");
		}
		if(props.containsKey("environment")){
			env = (String) props.get("environment");
		}
		if(props.containsKey("ssl")){
			ssl = Boolean.parseBoolean((String) props.get("ssl"));
		}

		if(props.containsKey("appVersion")){
			appVersion = (String) props.get("appVersion");
		}

		if(props.containsKey("background")){
			boolean background = Boolean.parseBoolean((String) props.get("background"));
			if(background){
				_notifier = new BackgroundNotifier();
			}
		}
	}
	
	public String getAppId(){
		return appId;
	}
	
	/*
	 * Send error to Jexbox server instantly
	 */
	public void send(JsonObject json) throws TransportException, UnsupportedEncodingException {
		Transport.send(json);
	}
	
	/*
	 * Entry point for sending errors to Jexbox.
	 * Depending from configuration, can send error instantly in current thread or use background notifier to put errors in queue
	 */
	public void send(Throwable e){
		sendWithMeta(e, null);
	}
	
	public void sendWithMeta(Throwable e, Map<String, Map<String, String>> metaD){
		JsonObject json = json(e, metaD);
		try {
			_notifier.send(json);
		} catch (UnsupportedEncodingException e1) {
			_logger.log(Level.SEVERE, "Could not able to send error to Jexbox", e1);
		} catch (TransportException e1) {
			_logger.log(Level.SEVERE, "Could not able to send error to Jexbox", e1);
		}
	}
	
	public JsonObject json(Throwable e){
		return json(e, null);
	}
	
	public JsonObject json(Throwable e, Map<String, Map<String, String>> metaD){
		JsonObject json = new JsonObject();
		json.add("appId", new JsonPrimitive(getAppId()));
		json.add("host", new JsonPrimitive(getHttpHost()));
		json.add("appVersion", new JsonPrimitive(getAppVersion()));

		JsonObject notifier = new JsonObject();
		json.add("notifier", notifier);
		notifier.add("name", new JsonPrimitive(getName()));
		notifier.add("version", new JsonPrimitive(getVersion()));
		notifier.add("url", new JsonPrimitive(getUrl()));
		notifier.add("env", new JsonPrimitive(getEnv()));
		
		JsonArray exceptions = new JsonArray();
		json.add("exceptions", exceptions);
		json.add("exceptionType", new JsonPrimitive(e.getClass().getName()));
		json.add("inClass", new JsonPrimitive(e.getStackTrace()[0].getClassName()));
		json.add("inMethod", new JsonPrimitive(e.getStackTrace()[0].getMethodName()));
		json.add("inFile", new JsonPrimitive(convertNull(e.getStackTrace()[0].getFileName())));
		json.add("onLine", new JsonPrimitive(e.getStackTrace()[0].getLineNumber()));
		Throwable ex = e;
        while(ex != null) {
    		JsonObject jex = new JsonObject();
    		exceptions.add(jex);
    		jex.add("class", new JsonPrimitive(ex.getClass().getName()));
    		String message = (ex.getMessage() == null || ex.getMessage().length() == 0) ? "" :  ex.getMessage();
    		jex.add("message", new JsonPrimitive(message));
    		
    		JsonArray stack = new JsonArray();
    		jex.add("stacktrace", stack);
    		
    		StackTraceElement[] stackTrace = ex.getStackTrace();
            for(int i=0; i < stackTrace.length; i++) {
            	StackTraceElement el = stackTrace[i];
            	stack.add(new JsonPrimitive(el.toString()));
            }
            
            ex = ex.getCause();
        }
        
		JsonObject meta = new JsonObject();
		json.add("meta", meta);
		
		JsonObject env = getEnvironment();
		meta.add("Environment", env);

		JsonObject systemProps = getSystemProps();
		meta.add("SystemProps", systemProps);
        
		if(metaD != null){
			for (String metaName : metaD.keySet()) {
				JsonObject metaP = new JsonObject();
				meta.add("metaName", metaP);

				Map<String, String> metaG = metaD.get(metaName);
				for (String key : metaG.keySet()) {
					String val = metaG.get(key);
					env.add(key, new JsonPrimitive(val));
				}			
			}
		}
		
		return json;
	}
	
	protected String convertNull(String prop){
		if(prop == null || prop.length() == 0) prop = "undefined";
		return prop;
	}
	
	protected JsonObject getEnvironment(){
		JsonObject env = new JsonObject();
		
		Set<String> keys = System.getenv().keySet();
		for (String key : keys) {
			String val = System.getenv(key);
			env.add(key, new JsonPrimitive(val));
		}
		
		return env;
	}
	
	protected JsonObject getSystemProps(){
		JsonObject env = new JsonObject();
		
		Set keys = System.getProperties().keySet();
		for (Object keyO : keys) {
			String key = keyO.toString();
			String val = System.getProperty(key);
			env.add(key, new JsonPrimitive(val));
		}
		
		return env;
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

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
}
