package de.aquadiva.joyce.base.util;

public class JoyceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1608225562132429158L;

	public JoyceException() {
	}

	public JoyceException(String message) {
		super(message);
	}

	public JoyceException(Throwable cause) {
		super(cause);
	}

	public JoyceException(String message, Throwable cause) {
		super(message, cause);
	}

	public JoyceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
