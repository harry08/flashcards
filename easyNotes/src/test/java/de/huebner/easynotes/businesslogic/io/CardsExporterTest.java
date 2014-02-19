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

	private static final String WORD_DEL_CUSTOM = "<WORD>";

	private static final String RECORD_DEL_CUSTOM = "<RECORD>";
	
	private static final String WORD_DEL_CSV = CommonConstants.SEMICOLON;

	private static final String RECORD_DEL_CSV = CommonConstants.END_OF_LINE;

	@Before
	public void setup() {
		cardsExporter = new CardsExporter();
	}

	/**
	 * Tests exporting a list of cards. The export string should be created
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

		CardExportData exportData = cardsExporter.generateExportString(
				cardList, CommonConstants.EXPORTTYPE_FRONT_BACKDESC,
				WORD_DEL_CUSTOM, RECORD_DEL_CUSTOM, false, true);
		assertEquals(3, exportData.getNrOfCards());
		String exportedString = exportData.getExportString();
		// Test occurrences of RECORD_DEL
		int countRecords = countDelimiter(exportedString, RECORD_DEL_CUSTOM);
		assertEquals(4, countRecords);
		// Test occurrences of specific Strings
		assertEquals(
				true,
				exportedString
						.contains("Front3<WORD>Back3 - Without descriptiontext<RECORD>"));
	}

	/**
	 * Tests exporting a card with a backtext containing a double-quote. The
	 * resulting string is supposed to be double-quoted.
	 */
	@Test
	public void shouldExportCardWithDoubleQuoteInText() {
		List<Card> cardList = new ArrayList<Card>();

		Card card1 = new Card();
		card1.setFrontText("Front1");
		String backtext = "Backtext1 with doublequote " + "\"" + " in text.";
		card1.setBackText(backtext);
		card1.setText("Text1");
		cardList.add(card1);

		CardExportData exportData = cardsExporter.generateExportString(
				cardList, CommonConstants.EXPORTTYPE_STANDARD, WORD_DEL_CUSTOM,
				RECORD_DEL_CUSTOM, true, false);

		assertEquals(1, exportData.getNrOfCards());
		String exportedString = exportData.getExportString();
		String expectedString = CommonConstants.DOUBLE_QUOTE
				+ "Backtext1 with doublequote "
				+ CommonConstants.MASEKD_DOUBLE_QUOTE + " in text."
				+ CommonConstants.DOUBLE_QUOTE;
		assertEquals(true, exportedString.contains(expectedString));
	}
	
	/**
	 * Tests exporting a card with a backtext containing a delimiter. The
	 * resulting string is supposed to be double-quoted.
	 */
	@Test
	public void shouldExportCardWithDelimiterInText() {
		List<Card> cardList = new ArrayList<Card>();
		
		Card card1 = new Card();
		card1.setFrontText("Front1");
		String backtext = "Backtext1 with delimiter " + RECORD_DEL_CSV + " in text.";
		card1.setBackText(backtext);
		card1.setText("Text1");
		cardList.add(card1);
		
		CardExportData exportData = cardsExporter.generateExportString(
				cardList, CommonConstants.EXPORTTYPE_STANDARD, WORD_DEL_CSV,
				RECORD_DEL_CSV, true, false);

		assertEquals(1, exportData.getNrOfCards());
		String exportedString = exportData.getExportString();
		String expectedString = CommonConstants.DOUBLE_QUOTE + backtext
				+ CommonConstants.DOUBLE_QUOTE;
		assertEquals(true, exportedString.contains(expectedString));
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
