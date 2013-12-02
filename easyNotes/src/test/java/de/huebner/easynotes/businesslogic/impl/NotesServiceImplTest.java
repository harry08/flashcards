package de.huebner.easynotes.businesslogic.impl;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.huebner.easynotes.FileUtils;
import de.huebner.easynotes.UnitTestConstants;
import de.huebner.easynotes.businesslogic.NotesServiceBusinessException;
import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Notebook;

public class NotesServiceImplTest {

	private NotesServiceImpl notesService;

	private static EntityManager em;

	private static EntityManagerFactory emf;

	private static EntityTransaction tx;

	@BeforeClass
	public static void initEntityManager() throws Exception {
		emf = Persistence.createEntityManagerFactory(UnitTestConstants.TEST_UNIT_NAME);
		em = emf.createEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() throws SQLException {
		if (em != null) {
			em.close();
		}
		if (emf != null) {
			emf.close();
		}
	}

	@Before
	public void setup() {
		tx = em.getTransaction();
		notesService = new NotesServiceImpl();
		notesService.setEntityManager(em);
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

		// Persists the book to the database
		tx.begin();
		notebook = notesService.updateNotebook(notebook);
		card1 = notesService.updateCard(card1);
		card2 = notesService.updateCard(card2);
		tx.commit();
		assertNotNull("ID should not be null", card1.getId());
		assertNotNull("ID should not be null", card2.getId());
		// Retrieves all the cards from the database
		List<Card> cards = notesService.getCardsOfNotebook(notebook);
		assertEquals(2, cards.size());
		Card createdCard = cards.get(0);
		System.out.println("Card 1: " + createdCard.toString());
		createdCard = cards.get(1);
		System.out.println("Card 2: " + createdCard.toString());
	}

	@Test
	public void shouldImportCardsFromFile() throws Exception {
		// Create notebook
		Notebook notebook = new Notebook();
		notebook.setTitle("English III");
		tx.begin();
		notebook = notesService.updateNotebook(notebook);
		tx.commit();

		// Import cards
		String filename = "/English_III.csv";
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
		assertEquals(128, cardsOfNotebook.size());
	}
}
