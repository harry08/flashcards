package de.huebner.easynotes.businesslogic.io;

/**
 * Exception that shows Errors for import or export notes.
 */
public class CardsImporterException extends Exception {
	
	public static final int GENERAL_ERROR = 1;
	public static final int FORMAT_ERROR = 2;	
	public static final int UNEXPECTED_END_ERROR = 3;
	public static final int MORE_FIELDS = 4;
	public static final int UNEXPECTED_HEADER = 5;
	public static final int UNEXPECTED_FIELD = 6;

	private static final long serialVersionUID = -2559914602624571448L;
	
	private int errorCode;
	
	private String field;
	
	private String value;

	public CardsImporterException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
		this.field = "";
		this.value = "";
	}
	
	public CardsImporterException(String message, int errorCode, String field, String value) {
		super(message);
		this.errorCode = errorCode;
		this.field = field;
		this.value = value;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getField() {
		return field;
	}

	public String getValue() {
		return value;
	}
}
