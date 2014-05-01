package de.huebner.easynotes.businesslogic.impl;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.huebner.easynotes.FileUtils;
import de.huebner.easynotes.UnitTestConstants;
import de.huebner.easynotes.businesslogic.NotesServiceBusinessException;
import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Category;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.common.CommonConstants;

public class NotesServiceImplTest {

	private NotesServiceImpl notesService;

	private static EntityManager em;

	private static EntityManagerFactory emf;

	private static EntityTransaction tx;

	@Before
  public void setup() {
    emf = Persistence.createEntityManagerFactory(UnitTestConstants.TEST_UNIT_NAME);
    em = emf.createEntityManager();
    
    tx = em.getTransaction();
    notesService = new NotesServiceImpl();
    notesService.setEntityManager(em);
  }
	
  @After
  public void treardown() throws SQLException {
    if (em != null) {
      em.close();
    }
    if (emf != null) {
      emf.close();
    }
  }
	
	@Test
	public void shouldCreateNotebooks() throws Exception {
		Category cat1 = new Category();
		cat1.setTitle("Category I");
		Category cat2 = new Category();
		cat2.setTitle("Category II");

		Notebook notebook1 = new Notebook();
		notebook1.setTitle("Cards I");
		notebook1.addCategoryToNotebook(cat1);
		notebook1.addCategoryToNotebook(cat2);
		Notebook notebook2 = new Notebook();
		notebook2.setTitle("Cards II");
		Notebook notebook3 = new Notebook();
		notebook3.setTitle("Cards III");
		notebook3.addCategoryToNotebook(cat1);

		// Persist the notebooks to the database
		tx.begin();
		cat1 = notesService.updateCategory(cat1);
		cat2 = notesService.updateCategory(cat2);
		notebook1 = notesService.updateNotebook(notebook1);
		notebook2 = notesService.updateNotebook(notebook2);
		notebook3 = notesService.updateNotebook(notebook3);
		tx.commit();

		// Check newly created data
		List<Category> categories = notesService.getAllCategories();
    assertEquals(2, categories.size());
		List<Notebook> notebooksCat1 = notesService.getAllNotebooks(cat1);
		assertEquals(2, notebooksCat1.size());
		List<Notebook> notebooksCat2 = notesService.getAllNotebooks(cat2);
		assertEquals(1, notebooksCat2.size());
	}
	
	@Test
	public void shouldDeleteCategory() {
		Category cat1 = new Category();
		cat1.setTitle("Category I");
		Category cat2 = new Category();
		cat2.setTitle("Category II");

		Notebook notebook1 = new Notebook();
		notebook1.setTitle("Cards I");
		notebook1.addCategoryToNotebook(cat1);
		notebook1.addCategoryToNotebook(cat2);
		Notebook notebook2 = new Notebook();
		notebook2.setTitle("Cards II");
		Notebook notebook3 = new Notebook();
		notebook3.setTitle("Cards III");
		notebook3.addCategoryToNotebook(cat1);

		// Persist the notebooks to the database
		tx.begin();
		cat1 = notesService.updateCategory(cat1);
		cat2 = notesService.updateCategory(cat2);
		notebook1 = notesService.updateNotebook(notebook1);
		notebook2 = notesService.updateNotebook(notebook2);
		notebook3 = notesService.updateNotebook(notebook3);
		tx.commit();
		
		// Check newly created data
		List<Category> categories = notesService.getAllCategories();
		assertEquals(2, categories.size());
		List<Notebook> notebooksCat1 = notesService.getAllNotebooks(cat1);
		assertEquals(2, notebooksCat1.size());
		Collection<Category> catsNotebook1 = notebook1.getCategories();
		assertEquals(2, catsNotebook1.size());
		long notebook1Id = notebook1.getId();
		
		// Delete Category I
		tx.begin();
		notesService.deleteCategory(cat1);
		tx.commit();
		
		// Get data again and check
		categories = notesService.getAllCategories();
		assertEquals(1, categories.size());		
		notebook1 = notesService.getNotebook(notebook1Id);
		assertEquals(1, notebook1.getCategories().size());
	}

	@Test
	public void shouldCreateCards() throws Exception {
		Notebook notebook = new Notebook();
		notebook.setTitle("Cards I");

		Card card1 = new Card();
		card1.setNotebook(notebook);
		card1.setFrontText("Front1");
		card1.setBackText("Back1");
		card1.setText("Text1");

		Card card2 = new Card();
		card2.setNotebook(notebook);
		card2.setFrontText("Front2");
		card2.setBackText("Back2");
		card2.setText("Text2");

		// Persist the notebook and cards to the database
		tx.begin();
		notebook = notesService.updateNotebook(notebook);
		card1 = notesService.updateCard(card1);
		card2 = notesService.updateCard(card2);
		tx.commit();
		assertNotNull("ID should not be null", card1.getId());
		assertNotNull("ID should not be null", card2.getId());
		
		// Retrieve all the cards from the database and check
		List<Card> cards = notesService.getCardsOfNotebook(notebook);
		assertEquals(2, cards.size());
		Card createdCard = cards.get(0);
		System.out.println("Card 1: " + createdCard.toString());
		createdCard = cards.get(1);
		System.out.println("Card 2: " + createdCard.toString());
	}

	/**
	 * Imports the file CardsImporterTest2.csv. The file contains 10 cards.
	 * Only fields fronttext, backtext and description.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldImportCardsFromFile() throws Exception {
		// Create notebook
		Notebook notebook = new Notebook();
		notebook.setTitle("English");
		tx.begin();
		notebook = notesService.updateNotebook(notebook);
		tx.commit();

		// Import cards
		String filename = "/CardsImporterTest2.csv";
		String inputString = new FileUtils().getFileContent(filename);
		tx.begin();
		try {
			notesService.importCards(inputString, notebook);
		} catch (NotesServiceBusinessException e) {
			fail("Exception importing cards: " + e.toString());
		} finally {
			if (!tx.getRollbackOnly()) {
				tx.commit();
			}
		}

		// Check
		List<Card> cardsOfNotebook = notesService.getCardsOfNotebook(notebook);
		assertEquals(10, cardsOfNotebook.size());
	}
	
	/**
	 * Imports the file CardsImportAllFieldsTest2.csv. The file contains learn
	 * progress.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldImportCardsFromFileWithAllFields() throws Exception {
		// Create notebook
		Notebook notebook = new Notebook();
		notebook.setTitle("English");
		tx.begin();
		notebook = notesService.updateNotebook(notebook);
		tx.commit();

		// Import cards
		String filename = "/CardsImporterAllFieldsTest2.csv";
		String inputString = new FileUtils().getFileContent(filename);
		tx.begin();
		try {
			notesService.importCards(inputString, notebook);
		} catch (NotesServiceBusinessException e) {
			fail("Exception importing cards: " + e.toString());
		} finally {
			if (!tx.getRollbackOnly()) {
				tx.commit();
			}
		}

		// Check
		List<Card> cardsOfNotebook = notesService.getCardsOfNotebook(notebook);
		assertEquals(3, cardsOfNotebook.size());
		Card card1 = getCard(cardsOfNotebook, "sales pitch");
		assertEquals(9, card1.getCompartment());
		assertNull(card1.getNextScheduled());
		Card card2 = getCard(cardsOfNotebook, "stride");
		assertEquals(2, card2.getCompartment());
		String nextScheduledString = CommonConstants.TIMESTAMP_FMT.format(card2.getNextScheduled());
		assertEquals("2014.03.08 07:12:06", nextScheduledString);
		Card card3 = getCard(cardsOfNotebook, "rupture");
		assertEquals(3, card3.getCompartment());
		nextScheduledString = CommonConstants.TIMESTAMP_FMT.format(card3.getNextScheduled());
		assertEquals("2014.03.25 17:22:34", nextScheduledString);
	}
	
	private Card getCard(List<Card> cards, String frontText) {
		for (Card card : cards) {
			if (card.getFrontText().equals(frontText)) {
				return card;
			}
		}
		
		return null;
	}
}
