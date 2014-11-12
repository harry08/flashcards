package de.huebner.easynotes.client;

/**
 * Exceptions of Easynotes client
 */
public class EasynotesException extends Exception {
	
	private static final long serialVersionUID = 4598850499813365259L;
	
	private int statusCode;
	
	public EasynotesException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
}
