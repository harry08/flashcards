package de.huebner.easynotes.businesslogic.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.common.CommonConstants;

public class CardsExporterTest {

	private CardsExporter cardsExporter;

	private static final String WORD_DEL = "<WORD>";

	private static final String RECORD_DEL = "<RECORD>";

	@Before
	public void setup() {
		cardsExporter = new CardsExporter();
	}

	/**
	 * Tests exporting a list of cards. The exportstring should be created
	 * successfully.
	 */
	@Test
	public void shouldExportCardList() {
		List<Card> cardList = new ArrayList<Card>();

		Card card1 = new Card();
		card1.setFrontText("Front1");
		card1.setBackText("Back1");
		card1.setText("Text1");
		cardList.add(card1);

		Card card2 = new Card();
		card2.setFrontText("Front2");
		card2.setBackText("Back2");
		card2.setText("Text2");
		cardList.add(card2);
		
		Card card3 = new Card();
		card3.setFrontText("Front3");
		card3.setBackText("Back3 - Without descriptiontext");
		cardList.add(card3);

		CardExportData exportData = cardsExporter.generateExportString(cardList, CommonConstants.EXPORTTYPE_FRONT_BACKDESC, WORD_DEL,
				RECORD_DEL);
		assertEquals(3, exportData.getNrOfCards());
		String exportedString = exportData.getExportString();
		// Test occurrences of RECORD_DEL
		int countRecords = countDelimiter(exportedString, RECORD_DEL);
		assertEquals(3, countRecords);
		// Test occurrences of specific Strings
		assertEquals(true, exportedString.contains("Front3<WORD>Back3 - Without descriptiontext<RECORD>"));
	}

	/**
	 * Counts the occurrences of the given delimiter in the testString.
	 * 
	 * @param testString
	 * @param delimiter
	 * @return nr of occurrences of the delimiter.
	 */
	private int countDelimiter(String testString, String delimiter) {
		int count = 0;

		int currentIndex = 0;
		int maxIndex = testString.length() - 1;

		while (currentIndex <= maxIndex) {
			int foundIndex = testString.indexOf(delimiter, currentIndex);
			if (foundIndex > -1) {
				count++;
				currentIndex = foundIndex + delimiter.length();
			} else {
				currentIndex = maxIndex + 1;
			}
		}

		return count;
	}
}
