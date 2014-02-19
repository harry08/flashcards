package de.huebner.easynotes.businesslogic.io;

import java.util.ArrayList;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.common.CommonConstants;

/**
 * Exports a list of cards to a string.
 */
public class CardsExporter {

	private String wordDelimiter;

	private String recordDelimiter;

	private int exportType;

	private int exportCount;

	private List<String> exportFields;

	/**
	 * if set to YES, double-quotes will be put around a word if the word
	 * contains delimiters or double-quotes.
	 */
	private boolean doubleQuoteDelimiters;
	
	private boolean containsHeader;

	private String getHeaderLine() {
		StringBuffer line = new StringBuffer();
		
		int i = 0;
		for (String currentField : exportFields) {
			String word = currentField;
			
			line.append(word);
			if (i < exportFields.size() - 1) {
				line.append(wordDelimiter);
			}

			i++;
		}
		
		line.append(recordDelimiter);
		
		return line.toString();
	}
	
	/**
	 * Creates the export string for the given card.
	 * 
	 * @param currentCard
	 *            card to export
	 * @return export string
	 */
	private String getLineFromCard(Card currentCard) {
		StringBuffer line = new StringBuffer();

		String frontText = currentCard.getFrontText();
		if (frontText == null) {
			frontText = "";
		}
		String backText = currentCard.getBackText();
		if (backText == null) {
			backText = "";
		}
		String text = currentCard.getText();
		if (text == null) {
			text = "";
		}

		int i = 0;
		for (String currentField : exportFields) {
			boolean needDoubleQuote = false;
			String word = "";

			if (currentField.equals(CommonConstants.FIELD_FRONTTEXT)) {
				word = frontText;
			} else if (currentField.equals(CommonConstants.FIELD_BACKTEXT)) {
				word = backText;
			} else if (currentField.equals(CommonConstants.FIELD_DESCRIPTION)) {
				word = text;
			} else if (currentField.equals(CommonConstants.FIELD_BACKDESC)) {
				StringBuffer combinedBack = new StringBuffer();
				combinedBack.append(backText);
				if (text.length() > 0) {
					// description text available in this card. Append it
					// directly to the backtext.
					combinedBack.append(CommonConstants.END_OF_LINE);
					combinedBack.append(text);
				}

				word = combinedBack.toString();
			} else if (currentField.equals(CommonConstants.FIELD_ANSWER)) {
				word = String.valueOf(currentCard.getAnswer());
			} else if (currentField.equals(CommonConstants.FIELD_CREATED)) {
				String created = CommonConstants.TIMESTAMP_FMT.format(currentCard.getCreated());
				word = String.valueOf(created);
			} else if (currentField.equals(CommonConstants.FIELD_MODIFIED)) {
				String modified = CommonConstants.TIMESTAMP_FMT.format(currentCard.getModified());
				word = String.valueOf(modified);
			} else if (currentField.equals(CommonConstants.FIELD_LASTLEARNED)) {
				if (currentCard.getLastLearned() != null) {
					String learned = CommonConstants.TIMESTAMP_FMT.format(currentCard.getLastLearned());
					word = String.valueOf(learned);
				}
			} else if (currentField.equals(CommonConstants.FIELD_NEXTSCHEDULED)) {
				if (currentCard.getNextScheduled() != null) {
					String scheduled = CommonConstants.TIMESTAMP_FMT.format(currentCard.getNextScheduled());
					word = String.valueOf(scheduled);
				}
			} else if (currentField.equals(CommonConstants.FIELD_NRCORRECT)) {
				word = String.valueOf(currentCard.getNrOfCorrect());
			} else if (currentField.equals(CommonConstants.FIELD_NRCORRECTTOTAL)) {
				word = String.valueOf(currentCard.getNrOfCorrectTotal());
			} else if (currentField.equals(CommonConstants.FIELD_NRWRONG)) {
				word = String.valueOf(currentCard.getNrOfWrong());
			} else if (currentField.equals(CommonConstants.FIELD_NRWRONGTOTAL)) {
				word = String.valueOf(currentCard.getNrOfWrongTotal());
			} else if (currentField.equals(CommonConstants.FIELD_COMPARTMENT)) {
				word = String.valueOf(currentCard.getCompartment());
			} 

			// Check for occurrence of double-quote in the word.
			// If so, put an extra double-quote before each found symbol and
			// double-quote the whole word.
			if (doubleQuoteDelimiters) {
				String[] wordItems = word.split(CommonConstants.DOUBLE_QUOTE);
				if (wordItems.length > 1) {
					StringBuffer newWord = new StringBuffer();
					needDoubleQuote = true;

					for (int y = 0; y < wordItems.length; y++) {
						String currentWordItem = wordItems[y];

						if (y > 0) {
							newWord.append(CommonConstants.MASEKD_DOUBLE_QUOTE);
						}
						newWord.append(currentWordItem);
					}

					word = newWord.toString();
				}
				
				// Check for occurrence of delimiter symbols in word. If so,
				// double-quote the whole word
				if (!needDoubleQuote) {
					boolean containsDelimiter = word.contains(wordDelimiter);
					if (!containsDelimiter) {
						containsDelimiter = word.contains(recordDelimiter);
					}				
					
					needDoubleQuote = containsDelimiter;
				}
			}
		
			if (needDoubleQuote) {
				word = new StringBuffer().append(CommonConstants.DOUBLE_QUOTE)
						.append(word).append(CommonConstants.DOUBLE_QUOTE)
						.toString();
			}

			line.append(word);
			if (i < exportFields.size() - 1) {
				line.append(wordDelimiter);
			}

			i++;
		}

		line.append(recordDelimiter);

		// Sanitize String from trailing word delimiters
		// TODO

		return line.toString();
	}

	/**
	 * Generates the exportString from the given list of cards.
	 * 
	 * @param cards
	 * @param exportType
	 *            Defines which fields should be imported.
	 * @param wordDelimiter
	 * @param recordDelimiter
	 * @param quoteDelimiter
	 * @param containsHeader
	 * @return generated exportString
	 */
	public CardExportData generateExportString(List<Card> cards,
			int exportType, String wordDelimiter, String recordDelimiter, boolean quoteDelimiter, boolean containsHeader) {
		this.wordDelimiter = wordDelimiter;
		this.recordDelimiter = recordDelimiter;
		this.exportType = exportType;
		this.doubleQuoteDelimiters = quoteDelimiter;
		this.containsHeader = containsHeader;
		exportCount = 0;

		generateFieldsString(exportType);

		StringBuffer exportString = new StringBuffer();

		if (this.containsHeader) {
			exportString.append(getHeaderLine());
		}
		for (Card currentCard : cards) {
			exportString.append(getLineFromCard(currentCard));
			exportCount++;
		}

		return new CardExportData(exportCount, exportString.toString());
	}

	private void generateFieldsString(int exportType2) {
		exportFields = new ArrayList<String>();

		switch (exportType) {
		case CommonConstants.EXPORTTYPE_STANDARD:
			exportFields.add(CommonConstants.FIELD_FRONTTEXT);
			exportFields.add(CommonConstants.FIELD_BACKTEXT);
			exportFields.add(CommonConstants.FIELD_DESCRIPTION);

			break;
		case CommonConstants.EXPORTTYPE_FRONTBACK:
			exportFields.add(CommonConstants.FIELD_FRONTTEXT);
			exportFields.add(CommonConstants.FIELD_BACKTEXT);

			break;
		case CommonConstants.EXPORTTYPE_BACKFRONT:
			exportFields.add(CommonConstants.FIELD_BACKTEXT);
			exportFields.add(CommonConstants.FIELD_FRONTTEXT);

			break;
		case CommonConstants.EXPORTTYPE_FRONT_BACKDESC:
			exportFields.add(CommonConstants.FIELD_FRONTTEXT);
			exportFields.add(CommonConstants.FIELD_BACKDESC);

			break;
		case CommonConstants.EXPORTTYPE_ALL_FIELDS:
			exportFields.add(CommonConstants.FIELD_FRONTTEXT);
			exportFields.add(CommonConstants.FIELD_BACKTEXT);
			exportFields.add(CommonConstants.FIELD_DESCRIPTION);
			exportFields.add(CommonConstants.FIELD_CREATED);
			exportFields.add(CommonConstants.FIELD_MODIFIED);
			exportFields.add(CommonConstants.FIELD_LASTLEARNED);
			exportFields.add(CommonConstants.FIELD_NEXTSCHEDULED);
			exportFields.add(CommonConstants.FIELD_ANSWER);
			exportFields.add(CommonConstants.FIELD_NRCORRECT);
			exportFields.add(CommonConstants.FIELD_NRCORRECTTOTAL);
			exportFields.add(CommonConstants.FIELD_NRWRONG);
			exportFields.add(CommonConstants.FIELD_NRWRONGTOTAL);
			exportFields.add(CommonConstants.FIELD_NRCORRECT);
			exportFields.add(CommonConstants.FIELD_NRCORRECTTOTAL);
			exportFields.add(CommonConstants.FIELD_COMPARTMENT);
			
			break;
		}
	}

	public int getExportCount() {
		return exportCount;
	}
}
