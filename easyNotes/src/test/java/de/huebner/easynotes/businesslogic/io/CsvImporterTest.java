package de.huebner.easynotes.businesslogic.io;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.huebner.easynotes.FileUtils;
import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.io.CsvImporter;
import de.huebner.easynotes.businesslogic.io.CardsImporterException;
import de.huebner.easynotes.common.CommonConstants;

public class CsvImporterTest {

	private CsvImporter cardsImporter;
	
	private ImportObjectCreator cardCreator; 

	@Before
	public void setup() {
		cardsImporter = new CsvImporter();
		cardCreator = new CardCreator();
		cardsImporter.setObjectCreator(cardCreator);
	}

	/**
	 * Tests importing of the file CardsImportTest.csv. The file contains a few
	 * cards with fronttext, backtext and description. The file should be
	 * imported successfully.
	 * 
	 * @throws CardsImporterException
	 */
	@Test
	public void shouldImportFileCardsImportTest() throws CardsImporterException {
		String filename = "/CardsImportTest.csv";
		String inputString = new FileUtils().getFileContent(filename);

		List<Card> cardList = cardsImporter.interpretInputString(inputString);
		assertEquals(3, cardList.size());
		Card card3 = cardList.get(2);
		assertEquals("Fronttext3", card3.getFrontText());
		assertEquals("Backtext3", card3.getBackText());
		assertEquals(
				"Description3;TextafterSemicolon;\"Text after Semicolon in Doublequotes.\"",
				card3.getText());
	}

	/**
	 * Tests importing of the file CardsImportAllFieldsTest.csv. The file
	 * contains a few cards with all fields. The file should be imported
	 * successfully.
	 * 
	 * @throws CardsImporterException
	 */
	@Test
	public void shouldImportFileCardsImportAllFieldsTest()
			throws CardsImporterException {
		String filename = "/CardsImportAllFieldsTest.csv";
		String inputString = new FileUtils().getFileContent(filename);

		List<Card> cardList = cardsImporter.interpretInputString(inputString);
		assertEquals(3, cardList.size());
		Card card3 = cardList.get(2);
		assertEquals("gorgeous", card3.getFrontText());
		assertEquals("wunderschoen", card3.getBackText());
		assertEquals(1, card3.getCompartment());
		assertEquals(1, card3.getNrOfCorrect());
		assertEquals(0, card3.getNrOfWrong());
		String lastLearnedString = CommonConstants.TIMESTAMP_FMT.format(card3.getLastLearned());
		assertEquals("2014.02.11 17:21:07", lastLearnedString);
		String nextScheduledString = CommonConstants.TIMESTAMP_FMT.format(card3.getNextScheduled());
		assertEquals("2014.02.12 17:21:07", nextScheduledString);
		String createdString = CommonConstants.TIMESTAMP_FMT.format(card3.getCreated());
		assertEquals("2013.12.18 05:58:38", createdString);
		String modifiedString = CommonConstants.TIMESTAMP_FMT.format(card3.getModified());
		assertEquals("2013.12.18 05:58:38", modifiedString);
	}

	/**
	 * Tests importing of the file English_III.csv. The file contains 128 cards.
	 * Some cards contain a description text.The file should be imported
	 * successfully.
	 * 
	 * @throws CardsImporterException
	 */
	@Test
	public void shouldImportFileEnglis_III() throws CardsImporterException {
		String filename = "/English_III.csv";
		String inputString = new FileUtils().getFileContent(filename);

		List<Card> cardList = cardsImporter.interpretInputString(inputString);
		assertEquals(128, cardList.size());
		Card card128 = cardList.get(127);
		assertEquals("sweep", card128.getFrontText());
		assertEquals("fegen", card128.getBackText());
	}

	@Test(expected = CardsImporterException.class)
	public void shouldCauseImportErrorMissingEndQuted() throws CardsImporterException {
		String filename = "/CardsImportErrorTest.csv";
		String inputString = new FileUtils().getFileContent(filename);

		try {
			cardsImporter.interpretInputString(inputString);
		} catch (CardsImporterException e) {
			assertEquals("Unexpected end of a card", e.getMessage());
			throw e;
		}
	}

	@Test(expected = CardsImporterException.class)
	public void shouldCauseImportErrorToMuchFields() throws CardsImporterException {
		String filename = "/CardsImportErrorTest_2.csv";
		String inputString = new FileUtils().getFileContent(filename);

		try {
			cardsImporter.interpretInputString(inputString);
		} catch (CardsImporterException e) {
			assertEquals("To much fields in record", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected = CardsImporterException.class)
	public void shouldCauseImportErrorWrongHeader() throws CardsImporterException {
		String filename = "/CardsImportErrorTest_3.csv";
		String inputString = new FileUtils().getFileContent(filename);

		try {
			cardsImporter.interpretInputString(inputString);
		} catch (CardsImporterException e) {
			assertEquals(CardsImporterException.UNEXPECTED_HEADER, e.getErrorCode());
			assertEquals("front text", e.getValue());
			throw e;
		}
	}
	
	@Test(expected = CardsImporterException.class)
	public void shouldCauseImportErrorWrongField() throws CardsImporterException {
		String filename = "/CardsImportErrorTest_4.csv";
		String inputString = new FileUtils().getFileContent(filename);

		try {
			cardsImporter.interpretInputString(inputString);
		} catch (CardsImporterException e) {
			assertEquals(CardsImporterException.UNEXPECTED_FIELD, e.getErrorCode());
			assertEquals("back text", e.getField());
			assertEquals("Backtext1", e.getValue());
			throw e;
		}
	}
	
	/**
	 * Tests importing of the file Phrases.csv. The file should be
	 * imported successfully. This file has quoted text with linebreaks.
	 * 
	 * @throws CardsImporterException
	 */
	@Test
	public void shouldImportFilePhrases() throws CardsImporterException {
		String filename = "/Phrases.csv";
		String inputString = new FileUtils().getFileContent(filename);

		try {
			List<Card> cardList = cardsImporter.interpretInputString(inputString);
			assertEquals(2, cardList.size());
		} catch (CardsImporterException e) {
			assertEquals("Unexpected end of a card", e.getMessage());
			throw e;
		}		
	}
}
