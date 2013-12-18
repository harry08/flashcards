package de.huebner.easynotes.businesslogic.io;

import java.util.ArrayList;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;

import static de.huebner.easynotes.common.CommonConstants.*;

/**
 * Imports a list of cards from a given csv string. The first line of the importstring has to contain the fields.
 */
public class CardsImporter {

	// Common States
	private static final int STATE_NOTDEF = -1;

	private static final int STATE_HEADER = 10;

	private static final int STATE_NOTEPART = 100;

	private static final int STATE_NOTEPART_NONQUOTED = 130;

	private static final int STATE_NOTEPART_QUOTED = 140;

	private static final int STATE_END = 999;

	// Modes
	private static final int IMPORT_MODE_NOTES = 1;

	private static final int IMPORT_MODE_CARDS = 2;

	private static final String HEADER_NOTES = FIELD_TITLE + SEMICOLON + FIELD_TEXT;

	private static final String HEADER_CARDS = FIELD_FRONTTEXT + SEMICOLON + FIELD_BACKTEXT
			+ SEMICOLON + FIELD_DESCRIPTION;

	/**
	 * Liste mit erzeugten Notes
	 */
	private List<Card> cardList;

	/**
	 * Modus des Imports. Eintraege werden entweder als Notes oder Cards
	 * interpretiert.
	 */
	private int importMode;

	/**
	 * Aktuell bearbeitete Expression
	 */
	private Card currentCard;

	private int currentNoteFieldIndex;

	/**
	 * Aktueller Index des String-Endes des zu untersuchenden ExpressionStrings
	 */
	private int endOfStringIndex;

	/**
	 * Current state
	 */
	private int currentState = STATE_NOTDEF;

	public CardsImporter() {
		cardList = new ArrayList<Card>();
	}

	/**
	 * Imports the given String and returns a list of notes.
	 * 
	 * @param importString
	 * @return generated notelist from import string
	 */
	public List<Card> interpretInputString(String importString) throws CardsImporterException {
		currentState = STATE_HEADER;

		currentCard = null;
		cardList.clear();

		endOfStringIndex = importString.length() - 1;
		int i = 0;
		while (currentState < STATE_END) {
			switch (currentState) {
			case STATE_HEADER:
				i = interpretHeader(importString, i);
				break;
			case STATE_NOTEPART:
				// Interpret String as part of a note.
				i = interpretNotepart(importString, i);

				break;
			default:
				// Unexpected state
				throw new CardsImporterException("General error importing cards");
			}

			i++;

			if (i >= endOfStringIndex) {
				if (currentState == STATE_NOTEPART_NONQUOTED || currentState == STATE_NOTEPART_QUOTED) {
					// String zu Ende, aber Note noch offen
					throw new CardsImporterException("Unexpected end of a card");
				}

				currentState = STATE_END;
			}
		}

		return cardList;
	}

	/**
	 * Interpretiert den String an der angegebenen Stelle als Header-Zeile.
	 * 
	 * @param importString
	 * @param beginIndex
	 * @return aktuelle Position im ImportString
	 */
	private int interpretHeader(String importString, int beginIndex) {
		int i = beginIndex;
		int localEndOfStringIndex = importString.length() - 1;

		boolean endOfExpression = false;

		StringBuffer headerValue = new StringBuffer();
		String nextChar = "";

		// Wert auslesen bis zum naechsten Zeilenende
		while (i <= localEndOfStringIndex && !endOfExpression) {
			i = maskCR(importString, i);
			nextChar = importString.substring(i, i + 1);
			if (nextChar.equals(END_OF_LINE)) {
				// Ende der Verarbeitung dieses parts
				endOfExpression = true;

				// Modus fuer zu importierende Daten setzen. Cards oder Notes.
				updateImportMode(headerValue.toString());

				currentState = STATE_NOTEPART;
			} else {
				headerValue.append(nextChar);
				i++;
			}
		}

		return i;
	}

	/**
	 * Interpretiert den String an der angegebenen Stelle als Teil einer Note.
	 * Dies kann ein Teil einer note in Hochkommas oder ein Teil einer note ohne
	 * Hochkommas sein.
	 * 
	 * @param importString
	 * @param i
	 * @return aktuelle Position im ImportString
	 */
	private int interpretNotepart(String importString, int i) throws CardsImporterException {
		if (importString.substring(i, i + 1).equals(DOUBLE_QUOTE)) {
			// Interpretiere den String als Teil einer Note. Der Teil ist in
			// Hochkommas.
			currentState = STATE_NOTEPART_QUOTED;
			if (currentCard == null) {
				// Anlage einer neuen Note.
				createNewNote();
				currentNoteFieldIndex = 0;
			}
			i += 1; // Quote ueberlesen
			i = interpretQuotedNotepart(importString, i);
		} else {
			// Interpretiere den String als Teil einer Note.
			currentState = STATE_NOTEPART_NONQUOTED;
			if (currentCard == null) {
				// Anlage einer neuen Note.
				createNewNote();
				currentNoteFieldIndex = 0;
			}
			i = interpretNonQuotedNotepart(importString, i);
		}

		return i;
	}

	/**
	 * Interpretiert eine Note-Information, die nicht in Hochkommas eingeschlossen
	 * ist. Es wird bis zum naechsten Semikolon oder bis zum Satzende gelesen.
	 * 
	 * @param importString
	 * @param beginIndex
	 * @return
	 */
	private int interpretNonQuotedNotepart(String importString, int beginIndex) throws CardsImporterException {
		int i = beginIndex;
		int localEndOfStringIndex = importString.length() - 1;

		boolean endOfExpression = false;

		StringBuffer notePartValue = new StringBuffer();
		String nextChar = "";

		// Wert auslesen bis zum naechsten Trennzeichen und in Note uebernehmen
		while (i <= localEndOfStringIndex && !endOfExpression) {
			i = maskCR(importString, i);
			nextChar = importString.substring(i, i + 1);
			if (nextChar.equals(SEMICOLON) || nextChar.equals(END_OF_LINE)) {
				endOfExpression = true;

				// Wert in note updaten
				updateCurrentNote(notePartValue.toString());

				if (nextChar.equals(SEMICOLON)) {
					// Note noch nicht zu Ende. Lese naechsten Note-Eintrag.
					currentNoteFieldIndex += 1;
					currentState = STATE_NOTEPART;
				} else if (nextChar.equals(END_OF_LINE)) {
					// Note zu Ende. Note in Liste aufnehmen.
					// Entweder folgen noch weitere Notes oder der Input ist am Ende.
					cardList.add(currentCard);
					currentCard = null;
					currentState = STATE_NOTEPART;
				}
			} else {
				notePartValue.append(nextChar);
				i++;
			}
		}

		return i;
	}

	/**
	 * Interpretiert eine Note-Information, die in Hochkommas eingeschlossen ist.
	 * Es wird bis zum naechsten Semikolon oder bis zum Satzende gelesen.
	 * 
	 * @param importString
	 * @param beginIndex
	 * @return
	 */
	private int interpretQuotedNotepart(String importString, int beginIndex) throws CardsImporterException {
		int i = beginIndex;
		int localEndOfStringIndex = importString.length() - 1;

		boolean endOfExpression = false;

		StringBuffer notePartValue = new StringBuffer();
		String nextChar = "";

		// Wert auslesen bis zum naechsten Trennzeichen und in Note uebernehmen
		while (i <= localEndOfStringIndex && !endOfExpression) {
			i = maskCR(importString, i);
			nextChar = importString.substring(i, i + 1);
			if (nextChar.equals(DOUBLE_QUOTE)) {
				int y = i + 1;
				y = maskCR(importString, y);
				nextChar = importString.substring(y, y + 1);
				if (nextChar.equals(SEMICOLON) || nextChar.equals(END_OF_LINE)) { // || nextChar.equals(CARRIAGE_RETURN)) {
					// Ende der Verarbeitung dieses parts
					i = y;
					endOfExpression = true;

					// Wert in note updaten
					updateCurrentNote(notePartValue.toString());

					if (nextChar.equals(SEMICOLON)) {
						// Note noch nicht zu Ende. Lese naechsten Note-Eintrag.
						currentNoteFieldIndex += 1;
						currentState = STATE_NOTEPART;
					} else if (nextChar.equals(END_OF_LINE)) { // || nextChar.equals(CARRIAGE_RETURN)) {
						// Note zu Ende. Note in Liste aufnehmen.
						// Entweder folgen noch weitere Notes oder der Input ist am Ende.
						cardList.add(currentCard);
						currentCard = null;
						currentState = STATE_NOTEPART;
					}
				} else if (nextChar.equals(DOUBLE_QUOTE)) {
					// Die beiden Double-quotes als ein Double-Quote aufnehmen
					notePartValue.append(nextChar);
					i += 2;
				} else {
					// Ende noch nicht erreicht. Zeichen mitaufnehmen
					notePartValue.append(nextChar);
					i++;
				}
			} else {
				notePartValue.append(nextChar);
				i++;
			}
		}

		return i;
	}

	private void createNewNote() {
		if (importMode == IMPORT_MODE_CARDS) {
			currentCard = new Card();
		}
	}

	private void updateImportMode(String headerValue) {
		String headerString = headerValue.trim();
		headerString = headerString.toLowerCase();
		
		if (headerString.equals(HEADER_NOTES)) {
			importMode = IMPORT_MODE_NOTES;
		} else if (headerString.equals(HEADER_CARDS)) {
			importMode = IMPORT_MODE_CARDS;
		} else {
			// Default mode
			importMode = IMPORT_MODE_CARDS;
		}
	}

	private int maskCR(String importString, int beginIndex) {
		int i = beginIndex;
		String nextChar = importString.substring(i, i + 1);

		if (nextChar.equals(CARRIAGE_RETURN)) {
			i += 1;
		}

		return i;
	}

	private void updateCurrentNote(String notePartValue) throws CardsImporterException {
		if (notePartValue.length() > 0) {
			if (importMode == IMPORT_MODE_CARDS) {
				if (currentNoteFieldIndex > 2) {
					throw new CardsImporterException("To much fields in record");
				}
				switch (currentNoteFieldIndex) {
				case 0:
					currentCard.setFrontText(notePartValue);
					break;
				case 1:
					currentCard.setBackText(notePartValue);
					break;
				case 2:
					currentCard.setText(notePartValue);
					break;
				}
			}
		}
	}
}
