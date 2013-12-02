package de.huebner.easynotes.businesslogic.io;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.huebner.easynotes.FileUtils;
import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.io.CardsImporter;
import de.huebner.easynotes.businesslogic.io.CardsImporterException;

public class CardsImporterTest {

	private CardsImporter cardsImporter;

	@Before
	public void setup() {
		cardsImporter = new CardsImporter();
	}

	/**
	 * Tests importing of the file CardsImportTest.csv. The file should be
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
		assertEquals("Description3;TextafterSemicolon;\"Text after Semicolon in Doublequotes.\"",
				card3.getText());
	}

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
