package de.huebner.easynotes.businesslogic.io;

import java.util.ArrayList;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;

/**
 * Imports values of a csv string. The first line of the import string is
 * interpreted as the header line containing the names of single fields. This
 * class uses ImportObjectCreator to create a concrete object from the values of
 * one record.
 * <br>
 * The class is implemented as a state transition engine.  
 */
public class CsvImporter {

	// State constants
	private static final int STATE_NOTDEF = -1;

	private static final int STATE_HEADER = 10;

	private static final int STATE_RECORD_PART = 100;

	private static final int STATE_RECORD_PART_NONQUOTED = 130;

	private static final int STATE_RECORD_PART_QUOTED = 140;

	private static final int STATE_END = 999;
	
	/**
     * constant for semicolon
     */
    private static final String SEMICOLON = ";";

    /**
     * constant for line feed
     */
    private static final String END_OF_LINE = "\n";

    /**
     * constant for carriage return
     */
    private static final String CARRIAGE_RETURN = "\r";
    
    /**
     * constant for double-quote
     */
    public static final String DOUBLE_QUOTE = "\"";
	
	/**
	 * List with created objects during import
	 */
	private List<Card> objectList;

	/**
	 * Current object. 
	 */
	private Card currentObject;
	
	/**
	 * Creator for objects
	 */
	private ImportObjectCreator objectCreator;

	private int currentFieldIndex;

	/**
	 * Index of the last element of the import string.
	 */
	private int endOfStringIndex;
	
	/**
	 * List with field names. Is filled after reading the header line of the csv string.
	 */
	private List<String> fieldNames;
	
	/**
	 * Current state
	 */
	private int currentState = STATE_NOTDEF;

	public CsvImporter() {
		objectList = new ArrayList<Card>();
		fieldNames = new ArrayList<String>();
	}
	
	/**
	 * Setter for concrete Object creator.
	 * 
	 * @param objectCreator
	 *            Object creator to set
	 */
	public void setObjectCreator(ImportObjectCreator objectCreator) {
		this.objectCreator = objectCreator;
	}

	/**
	 * Imports the given String and returns a list of objects.
	 * 
	 * @param importString
	 * @return list with objects created during import
	 * @throws CardsImporterException
	 *             Thrown when an error occurs importing the string.
	 */
	public List<Card> interpretInputString(String importString) throws CardsImporterException {
		currentState = STATE_HEADER;

		currentObject = null;
		objectList.clear();

		endOfStringIndex = importString.length() - 1;
		int i = 0;
		while (currentState < STATE_END) {
			switch (currentState) {
			case STATE_HEADER:
				i = interpretHeader(importString, i);
				break;
			case STATE_RECORD_PART:
				// Interprets string as part of a record
				i = interpretRecordPart(importString, i);

				break;
			default:
				// Unexpected state
				throw new CardsImporterException("General error importing cards", CardsImporterException.GENERAL_ERROR);
			}

			i++;

			if (i >= endOfStringIndex) {
				if (currentState == STATE_RECORD_PART_NONQUOTED || currentState == STATE_RECORD_PART_QUOTED) {
					// end of string reached; current field not finished
					throw new CardsImporterException("Unexpected end of a card", CardsImporterException.UNEXPECTED_END_ERROR);
				}

				currentState = STATE_END;
			}
		}

		return objectList;
	}

	/**
	 * Interprets the string at the given position as the header line. This line
	 * must contain the names of all fields in the record.
	 * 
	 * @param importString
	 * @param beginIndex
	 * @return new reading position
	 */
	private int interpretHeader(String importString, int beginIndex) throws CardsImporterException {
		int i = beginIndex;
		int localEndOfStringIndex = importString.length() - 1;

		boolean endOfExpression = false;

		StringBuffer headerValue = new StringBuffer();
		String nextChar = "";

		// read value up to the next end of line
		while (i <= localEndOfStringIndex && !endOfExpression) {
			i = maskCR(importString, i);
			nextChar = importString.substring(i, i + 1);
			if (nextChar.equals(END_OF_LINE)) {
				// End of expression of this part
				endOfExpression = true;
				
				extractHeaderFields(headerValue.toString());
				
				currentState = STATE_RECORD_PART;
			} else {
				headerValue.append(nextChar);
				i++;
			}
		}

		return i;
	}

	/**
	 * Interprets the string at the given position as a part of a record.
	 * 
	 * @param importString
	 * @param i
	 * @return new reading position
	 */
	private int interpretRecordPart(String importString, int i)
			throws CardsImporterException {
		if (importString.substring(i, i + 1).equals(DOUBLE_QUOTE)) {
			// Interpret the string as part of the record. This part is
			// in-between double-quotes.
			currentState = STATE_RECORD_PART_QUOTED;
			if (currentObject == null) {
				// create a new object
				createNewObject();
				currentFieldIndex = 0;
			}
			i += 1; // skip this double-quote
			i = interpretQuotedRecordPart(importString, i);
		} else {
			// Interpret the string as part of the record
			currentState = STATE_RECORD_PART_NONQUOTED;
			if (currentObject == null) {
				// create a new object
				createNewObject();
				currentFieldIndex = 0;
			}
			i = interpretNonQuotedRecordPart(importString, i);
		}

		return i;
	}

	/**
	 * Interprets a part of the record. This part is not double quoted. Reads up to
	 * the next delimiter. This could be a semicolon or a record end character.
	 * 
	 * @param importString
	 * @param beginIndex
	 * @return new reading position
	 */
	private int interpretNonQuotedRecordPart(String importString, int beginIndex) throws CardsImporterException {
		int i = beginIndex;
		int localEndOfStringIndex = importString.length() - 1;

		boolean endOfExpression = false;

		StringBuffer recordPartValue = new StringBuffer();
		String nextChar = "";

		// Read value up to next delimiter and update object with that value.
		while (i <= localEndOfStringIndex && !endOfExpression) {
			i = maskCR(importString, i);
			nextChar = importString.substring(i, i + 1);
			if (nextChar.equals(SEMICOLON) || nextChar.equals(END_OF_LINE)) {
				endOfExpression = true;

				updateRecordPart(recordPartValue.toString(), nextChar);
				
			} else {
				recordPartValue.append(nextChar);
				i++;
			}
		}

		return i;
	}

	/**
	 * Interprets a part of the record. This part is double quoted. Reads up to
	 * a double quote followed by a semicolon or record end delimiter. The
	 * content in-between is used as the value of this part.
	 * 
	 * @param importString
	 * @param beginIndex
	 * @return new reading position
	 */
	private int interpretQuotedRecordPart(String importString, int beginIndex)
			throws CardsImporterException {
		int i = beginIndex;
		int localEndOfStringIndex = importString.length() - 1;

		boolean endOfExpression = false;

		StringBuffer recordPartValue = new StringBuffer();
		String nextChar = "";

		// Read value up to next end delimiter and update object with that value.
		while (i <= localEndOfStringIndex && !endOfExpression) {
			i = maskCR(importString, i);
			nextChar = importString.substring(i, i + 1);
			if (nextChar.equals(DOUBLE_QUOTE)) {
				// Inspect the following character to decide how to handle this double quote.
				int y = i + 1;
				y = maskCR(importString, y);
				nextChar = importString.substring(y, y + 1);
				if (nextChar.equals(SEMICOLON) || nextChar.equals(END_OF_LINE)) { 
					// end of this part
					i = y;
					endOfExpression = true;

					updateRecordPart(recordPartValue.toString(), nextChar);
					
				} else if (nextChar.equals(DOUBLE_QUOTE)) {
					recordPartValue.append(nextChar);
					i += 2;
				} else {
					// end not yet reached. Add character
					recordPartValue.append(nextChar);
					i++;
				}
			} else {
				recordPartValue.append(nextChar);
				i++;
			}
		}

		return i;
	}
	
	private void updateRecordPart(String value, String nextChar)
			throws CardsImporterException {
		updateCurrentObject(value);

		if (nextChar.equals(SEMICOLON)) {
			// Record not yet finished. Read next part.
			currentFieldIndex += 1;
			currentState = STATE_RECORD_PART;
		} else if (nextChar.equals(END_OF_LINE)) {
			// Record finished. Add to list
			objectList.add(currentObject);
			currentObject = null;
			currentState = STATE_RECORD_PART;
		}
	}

	private void createNewObject() {
		currentObject = objectCreator.createNewObject();		
	}
	
	private void extractHeaderFields(String headerValue) throws CardsImporterException {
		String headerString = headerValue.trim();
		headerString = headerString.toLowerCase();
		
		String[] headerItems = headerString.split(SEMICOLON);
		for (int i = 0; i < headerItems.length; i++) {
			fieldNames.add(headerItems[i]);
		}
		
		objectCreator.setFieldNames(fieldNames);
	}

	private int maskCR(String importString, int beginIndex) {
		int i = beginIndex;
		String nextChar = importString.substring(i, i + 1);

		if (nextChar.equals(CARRIAGE_RETURN)) {
			i += 1;
		}

		return i;
	}

	/**
	 * Puts the given value into the object.
	 * 
	 * @param recordPartValue
	 *            value to update in the object.
	 * @throws CardsImporterException
	 *             if the object could not be updated. i.e., due to a wrong
	 *             format in the input value.
	 */
	private void updateCurrentObject(String value)
			throws CardsImporterException {
		if (currentFieldIndex > fieldNames.size() - 1) {
			throw new CardsImporterException("To much fields in record", CardsImporterException.MORE_FIELDS, "", value);
		}
		
		String field = fieldNames.get(currentFieldIndex);
		objectCreator.updateValue(field, value);
	}
}
