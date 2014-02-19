package de.huebner.easynotes.businesslogic.io;

import static de.huebner.easynotes.common.CommonConstants.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;

/**
 * Concrete ObjectCreator for creating cards
 */
public class CardCreator implements ImportObjectCreator {

	private static final int IMPORT_TYPE_NOTES = 1;

	private static final int IMPORT_TYPE_CARDS = 2;

	private Card currentCard;

	private int importType;

	private SimpleDateFormat sdfTimestamp = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss");

	public Card createNewObject() {
		currentCard = new Card();

		return currentCard;
	}

	public void setFieldNames(List<String> headerFields)
			throws CardsImporterException {
		String firstField = headerFields.get(0);
		if (firstField.equals(FIELD_TITLE)) {
			importType = IMPORT_TYPE_NOTES;
		} else if (firstField.equals(FIELD_FRONTTEXT)) {
			importType = IMPORT_TYPE_CARDS;
		} else {
			throw new CardsImporterException("Unexpected field in header",
					CardsImporterException.UNEXPECTED_HEADER, "", firstField);
		}
	}

	public void updateValue(String field, String value)
			throws CardsImporterException {
		if (value.length() > 0) {
			if (importType == IMPORT_TYPE_CARDS) {
				try {
					if (field.equals(FIELD_FRONTTEXT)) {
						currentCard.setFrontText(value);
					} else if (field.equals(FIELD_BACKTEXT)) {
						currentCard.setBackText(value);
					} else if (field.equals(FIELD_DESCRIPTION)) {
						currentCard.setText(value);
					} else if (field.equals(FIELD_CREATED)) {
						Date created = sdfTimestamp.parse(value);
						currentCard.setCreated(created);
					} else if (field.equals(FIELD_MODIFIED)) {
						Date modified = sdfTimestamp.parse(value);
						currentCard.setModified(modified);
					} else if (field.equals(FIELD_ANSWER)) {
						currentCard.setAnswer(Integer.valueOf(value));
					} else if (field.equals(FIELD_NRCORRECT)) {
						currentCard.setNrOfCorrect(Integer.valueOf(value));
					} else if (field.equals(FIELD_NRCORRECTTOTAL)) {
						currentCard.setNrOfCorrectTotal(Integer.valueOf(value));
					} else if (field.equals(FIELD_NRWRONG)) {
						currentCard.setNrOfWrong(Integer.valueOf(value));
					} else if (field.equals(FIELD_NRWRONGTOTAL)) {
						currentCard.setNrOfWrongTotal(Integer.valueOf(value));
					} else if (field.equals(FIELD_COMPARTMENT)) {
						currentCard.setCompartment(Integer.valueOf(value));
					} else if (field.equals(FIELD_LASTLEARNED)) {
						Date lastLearned = sdfTimestamp.parse(value);
						currentCard.setLastLearned(lastLearned);
					} else if (field.equals(FIELD_NEXTSCHEDULED)) {
						Date nextScheduled = sdfTimestamp.parse(value);
						currentCard.setNextScheduled(nextScheduled);
					} else {
						throw new CardsImporterException("Unexpected field",
								CardsImporterException.UNEXPECTED_FIELD, field,
								value);
					}

				} catch (NumberFormatException nfe) {
					throw new CardsImporterException(
							"Formaterror reading importvalues.",
							CardsImporterException.FORMAT_ERROR, field, value);
				} catch (ParseException pe) {
					throw new CardsImporterException(
							"Formaterror reading importvalues.",
							CardsImporterException.FORMAT_ERROR, field, value);
				}
			}
		}
	}
}
