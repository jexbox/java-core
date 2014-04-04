package com.jexbox.jsf;

import java.util.Properties;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

import com.jexbox.JexboxWebImpl;

public class JexboxExceptionHandlerFactory extends ExceptionHandlerFactory {
	private ExceptionHandlerFactory _parent;
	
	private JexboxWebImpl _jexbox = null;
	
	public JexboxExceptionHandlerFactory(ExceptionHandlerFactory parent) {
		_parent = parent;

		try{
			Properties props = new Properties();
			props.put("appId", "4075779468e162bd454449f32622dc989925f148");
			props.put("background", "false");
			_jexbox = new JexboxWebImpl(props);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	 
	@Override
	public ExceptionHandler  getExceptionHandler() {
		ExceptionHandler handler = new JexboxExceptionHandler(_jexbox, _parent.getExceptionHandler());
		return handler;
	}
	 
}
