package de.huebner.easynotes.businesslogic;

/**
 * BusinessException of NotesService
 */
public class NotesServiceBusinessException extends Exception {
	
  private static final long serialVersionUID = 8891029303781116561L;

  public NotesServiceBusinessException(String message) {
		super(message);
	}
}
