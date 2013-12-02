package de.huebner.easynotes.businesslogic.io;

/**
 * Exception that shows Errors for import or export notes.
 */
public class CardsImporterException extends Exception {

	private static final long serialVersionUID = -2559914602624571448L;

	public CardsImporterException(String message) {
		super(message);
	}
}
