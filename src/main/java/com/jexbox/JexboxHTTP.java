package com.jexbox;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JexboxHTTP extends Jexbox{
    private static Logger _logger = Logger.getLogger(JexboxHTTP.class.getName());
    
	public JexboxHTTP(Properties props) {
		super(props);
	}
	
	/*
	 * Entry point for sending errors to Jexbox.
	 * Depending from configuration, can send error instantly in current thread or use background notifier to put errors in queue
	 */
	public void send(Throwable e, HttpServletRequest request){
		sendWithMeta(e, request, null);
	}
	
	public void sendWithMeta(Throwable e, HttpServletRequest request, Map<String, Map<String, String>> metaD){
		try {
			JsonObject json = json(e, metaD);
			addRequestMetaData(request, json);
			addSessionMetaData(request, json);
			_notifier.send(json);
		} catch (UnsupportedEncodingException e1) {
			_logger.log(Level.SEVERE, "Could not able to send error to Jexbox", e1);
		} catch (TransportException e1) {
			_logger.log(Level.SEVERE, "Could not able to send error to Jexbox", e1);
		}
	}
	
	protected void addSessionMetaData(HttpServletRequest request, JsonObject json){
		HttpSession session = request.getSession(false);
		if(session != null){
			JsonObject meta = json.getAsJsonObject("meta");
			if(meta == null){
				meta = new JsonObject();
				json.add("meta", meta);
			}
			
			JsonObject sessionD = new JsonObject();
			meta.add("session", sessionD);
			
			Enumeration<String> names = session.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				Object attr = session.getAttribute(name);
				if(attr instanceof String){
					sessionD.add(name, new JsonPrimitive(attr.toString()));
				}
			}
		}
	}
	protected void addRequestMetaData(HttpServletRequest reqHTTP, JsonObject json){
			JsonObject meta = json.getAsJsonObject("meta");
			if(meta == null){
				meta = new JsonObject();
				json.add("meta", meta);
			}
			
			JsonObject req = new JsonObject();
			meta.add("request", req);
			
			Enumeration<String> names = reqHTTP.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				Object attr = reqHTTP.getAttribute(name);
				if(attr instanceof String){
					req.add(name, new JsonPrimitive(attr.toString()));
				}
			}
			
			names = reqHTTP.getParameterNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				String attr = reqHTTP.getParameter(name);
				req.add(name, new JsonPrimitive(attr.toString()));
			}
			
			names = reqHTTP.getHeaderNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				String attr = reqHTTP.getParameter(name);
				req.add(name, new JsonPrimitive(attr.toString()));
			}
			
			req.add("Auth Type", new JsonPrimitive(reqHTTP.getAuthType()));
			req.add("Character Encoding", new JsonPrimitive(reqHTTP.getCharacterEncoding()));
			req.add("Content Type", new JsonPrimitive(reqHTTP.getContentType()));
			req.add("Context Path", new JsonPrimitive(reqHTTP.getContextPath()));
			req.add("Local Addr", new JsonPrimitive(reqHTTP.getLocalAddr()));
			req.add("Local Name", new JsonPrimitive(reqHTTP.getLocalName()));
			req.add("Method", new JsonPrimitive(reqHTTP.getMethod()));
			req.add("Path Info", new JsonPrimitive(reqHTTP.getPathInfo()));
			req.add("Path Translated", new JsonPrimitive(reqHTTP.getPathTranslated()));
			req.add("Protocol", new JsonPrimitive(reqHTTP.getProtocol()));
			req.add("Query String", new JsonPrimitive(reqHTTP.getQueryString()));
			req.add("Remote Addr", new JsonPrimitive(reqHTTP.getRemoteAddr()));
			req.add("Remote Host", new JsonPrimitive(reqHTTP.getRemoteHost()));
			req.add("Remote User", new JsonPrimitive(reqHTTP.getRemoteUser()));
			req.add("Requested Session Id", new JsonPrimitive(reqHTTP.getRequestedSessionId()));
			req.add("Request URI", new JsonPrimitive(reqHTTP.getRequestURI()));
			req.add("Scheme", new JsonPrimitive(reqHTTP.getScheme()));
			req.add("Server Name", new JsonPrimitive(reqHTTP.getServerName()));
			req.add("Servlet Path", new JsonPrimitive(reqHTTP.getServletPath()));
			req.add("Content Length", new JsonPrimitive(reqHTTP.getContentLength()));
			
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
