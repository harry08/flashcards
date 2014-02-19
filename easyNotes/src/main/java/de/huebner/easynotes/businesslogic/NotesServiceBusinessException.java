package de.huebner.easynotes.businesslogic;

/**
 * BusinessException of NotesService
 */
public class NotesServiceBusinessException extends Exception {

	private static final long serialVersionUID = 8891029303781116561L;

	public static final int ERROR_IMPORT_GENERAL = 1;
	public static final int ERROR_IMPORT_WRONG_HEADER = 2;
	public static final int ERROR_IMPORT_UNEXPECTED_FIELD = 3;
	public static final int ERROR_IMPORT_WRONG_FORMAT = 4;
	public static final int ERROR_IMPORT_MORE_FIELDS_THAN_EXPECTED = 5;
	

	public static final int ERROR_IMPORT_WRITE_DATA = 6;

	private int errorCode;

	/**
	 * Detail information 1 <br>
	 * For data import errors this value represents the fieldname of the wrong
	 * field
	 */
	private String errorValue1;

	/**
	 * Detail information 2 <br>
	 * For data import errors this value represents the wrong value
	 */
	private String errorValue2;

	public NotesServiceBusinessException(String message) {
		super(message);
	}

	public NotesServiceBusinessException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public NotesServiceBusinessException(String message, int errorCode,
			String errorValue1, String errorValue2) {
		super(message);
		this.errorCode = errorCode;
		this.errorValue1 = errorValue1;
		this.errorValue2 = errorValue2;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorValue1() {
		return errorValue1;
	}

	public String getErrorValue2() {
		return errorValue2;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("NotesServiceBusinessException");
		sb.append("{errorCode=").append(errorCode);
		sb.append(", message='").append(getMessage()).append('\'');
		sb.append(", errorValue1='").append(errorValue1).append('\'');
		sb.append(", errorValue2='").append(errorValue2).append('}');

		return sb.toString();
	}
}
