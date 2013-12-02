package de.huebner.easynotes.businesslogic.io;

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

	private String getLineFromCard(Card currentCard) {
		String line = "";

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

		switch (exportType) {
		case CommonConstants.EXPORTTYPE_STANDARD:
			line = frontText + wordDelimiter + backText + wordDelimiter + text + recordDelimiter;
			break;
		case CommonConstants.EXPORTTYPE_FRONTBACK:
			line = frontText + wordDelimiter + backText + recordDelimiter;
			break;
		case CommonConstants.EXPORTTYPE_BACKFRONT:
			line = backText + wordDelimiter + frontText + recordDelimiter;
			break;
		case CommonConstants.EXPORTTYPE_FRONT_BACKDESC:
			StringBuffer combinedBack = new StringBuffer();
			combinedBack.append(backText);
			if (text.length() > 0) {	
				// description text available. Append it directly to the backtext.
				combinedBack.append(CommonConstants.END_OF_LINE);
				combinedBack.append(text);				
			} 
			line = frontText + wordDelimiter + combinedBack + recordDelimiter;
			
			break;
		}

		// Sanitize String from trailing word delimiters
		// TODO

		return line;
	}

	/**
	 * Generates the exportString from the given list of cards.
	 * 
	 * @param cards
	 * @param exportType
	 * @param wordDelimiter
	 * @param recordDelimiter
	 * @return generated exportString
	 */
	public CardExportData generateExportString(List<Card> cards, int exportType, String wordDelimiter,
			String recordDelimiter) {
		this.wordDelimiter = wordDelimiter;
		this.recordDelimiter = recordDelimiter;
		this.exportType = exportType;
		exportCount = 0;

		StringBuffer exportString = new StringBuffer();

		for (Card currentCard : cards) {
			exportString.append(getLineFromCard(currentCard));
			exportCount++;
		}

		return new CardExportData(exportCount, exportString.toString());
	}

	public int getExportCount() {
		return exportCount;
	}
}
