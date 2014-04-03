package com.jexbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.internal.renderers.ComponentResourcesRenderer;
import org.apache.tapestry5.internal.renderers.LocationRenderer;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.internal.OperationException;
import org.apache.tapestry5.ioc.services.ExceptionAnalysis;
import org.apache.tapestry5.ioc.services.ExceptionAnalyzer;
import org.apache.tapestry5.ioc.services.ExceptionInfo;
import org.apache.tapestry5.runtime.ComponentEventException;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.RequestGlobals;

public class JexboxRequestExceptionHandler implements RequestExceptionHandler{
	
	private RequestExceptionHandler _delegate;
	private JexboxWeb _jexbox;
	private RequestGlobals _request;
	private ExceptionAnalyzer _analyzer;
	
	public JexboxRequestExceptionHandler(RequestExceptionHandler delegate, JexboxWeb jexbox, RequestGlobals request, ExceptionAnalyzer analyzer){
		super();
		_delegate = delegate;
		_jexbox = jexbox;
		_request = request;
		_analyzer = analyzer;
	}
	
    public void handleRequestException(Throwable exception) throws IOException
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintWriter pw = new PrintWriter(baos);
    	MarkupWriterImpl mw = new MarkupWriterImpl(); 
    	ExceptionAnalysis analysis = _analyzer.analyze(exception);
    	List<ExceptionInfo> infos = analysis.getExceptionInfos();
    	mw.element("ul");

    	LocationRenderer locRend = new LocationRenderer();
    	ComponentResourcesRenderer crr = new ComponentResourcesRenderer(locRend);
    	for (ExceptionInfo info : infos) {
        	mw.element("li");
    		
        	mw.element("span", "class", "t-exception-class-name");
        	mw.write(info.getClassName());
        	mw.end();
    		
        	mw.element("div", "class", "t-exception-message");
        	mw.write(info.getMessage());
        	mw.end();

        	List<String> names = info.getPropertyNames();
    		for (String name : names) {
	        	mw.element("dl");
	        		mw.element("dt");
		        	mw.write(name);
        			mw.end();

	        		mw.element("dd");
		        	Object prop = info.getProperty(name);
		        	if(prop instanceof Location){
		        		Location loc = (Location) prop;
		        		locRend.render(loc, mw);
		        	}else if(prop instanceof Object[]){
		        		Object[] array = (Object[])prop;
		        		for (int i = 0; i < array.length; i++) {
							Object el = array[i];
							if(el instanceof ComponentResources){
				        		ComponentResources res = (ComponentResources) el;
				        		crr.render(res, mw);
							}
						}
		        	}else if(prop instanceof ComponentResources){
		        		ComponentResources res = (ComponentResources) prop;
		        		crr.render(res, mw);
		        	}else{
			        	mw.write(prop.toString());
		        	}
	        		mw.end();
    			mw.end();
			}
        	
    		mw.end();
		}
    	
    	mw.end();
    	
		mw.toMarkup(pw);
		pw.flush();
		baos.flush();
    	
		Map<String, String> meta = new HashMap<String, String>();
		String pageTrace = baos.toString();
		String pageTrace64 = Base64.encodeBase64String(pageTrace.getBytes());
		meta.put("data", pageTrace64);
		Map<String, Map<String, String>> meta2 = new HashMap<String, Map<String, String>>();
		meta2.put("Page Trace", meta);
		
		Throwable filtered = removeInfrastructure(exception);
		
    	_jexbox.sendWithMeta(filtered, _request.getHTTPServletRequest(), meta2);
    	_delegate.handleRequestException(exception);
    }
    
    protected Throwable removeInfrastructure(Throwable e){
    	if(e.getCause() != null){
    		if(e instanceof ComponentEventException){
    			return removeInfrastructure(e.getCause());
    		}else if(e instanceof OperationException){
    			return removeInfrastructure(e.getCause());
    		}else{
    			return e;
    		}
    	}else{
    		return e;
    	}
    }

}
