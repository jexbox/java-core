package com.jexbox;

public class TransportException extends Exception {
	private static final long serialVersionUID = -3599494320410133186L;

	public TransportException() {
		super();
	}

	public TransportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TransportException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransportException(String message) {
		super(message);
	}

	public TransportException(Throwable cause) {
		super(cause);
	}

}
