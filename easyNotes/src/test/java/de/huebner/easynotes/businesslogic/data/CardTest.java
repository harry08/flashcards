package de.huebner.easynotes.businesslogic.data;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.huebner.easynotes.UnitTestConstants;

/**
 * Tests the persistence of the data classes with direct usage of JPA functionality.
 */
public class CardTest {

	private static EntityManagerFactory emf;

	private static EntityManager em;

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
	public void initTransaction() {
		tx = em.getTransaction();
	}

	@Test
	public void shouldCreateCards() throws Exception {
		Category categoryLanguage = new Category();
		categoryLanguage.setTitle("Language");
		
		Notebook notebook = new Notebook();
		notebook.getCategories().add(categoryLanguage);
		notebook.setTitle("Cards I");
		notebook.setModified(new Date());

		Card card1 = new Card();
		card1.setNotebook(notebook);
		card1.setFrontText("Front1");
		card1.setBackText("Back1");
		card1.setText("Text1");
		card1.setModified(new Date());

		Card card2 = new Card();
		card2.setNotebook(notebook);
		card2.setFrontText("Front2");
		card2.setBackText("Back2");
		card2.setText("Text2");
		card2.setModified(new Date());

		// Persists the entities to the database
		tx.begin();
		em.persist(categoryLanguage);
		em.persist(notebook);
		em.persist(card1);
		em.persist(card2);
		tx.commit();
		assertNotNull("ID should not be null", card1.getId());
		// Retrieves the cards from the database
		@SuppressWarnings("unchecked")
		List<Category> categories = em.createNamedQuery("Category.findAllCategories").getResultList();
		assertEquals(1, categories.size());
		
		@SuppressWarnings("unchecked")
		List<Card> cards = em.createNamedQuery("Card.findAllCards").getResultList();
		assertEquals(2, cards.size());
		Card createdCard = cards.get(0);
		System.out.println("Card 1: " + createdCard.toString());
		createdCard = cards.get(1);
		System.out.println("Card 2: " + createdCard.toString());
	}
}
