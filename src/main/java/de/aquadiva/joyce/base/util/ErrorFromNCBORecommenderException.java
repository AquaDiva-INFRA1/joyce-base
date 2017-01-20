package de.aquadiva.joyce.base.util;

import de.aquadiva.joyce.base.util.JoyceException;

public class ErrorFromNCBORecommenderException extends JoyceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5898990056544111832L;

	public ErrorFromNCBORecommenderException() {
	}

	public ErrorFromNCBORecommenderException(String message) {
		super(message);
	}

	public ErrorFromNCBORecommenderException(Throwable cause) {
		super(cause);
	}

	public ErrorFromNCBORecommenderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ErrorFromNCBORecommenderException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
