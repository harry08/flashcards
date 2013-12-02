package de.huebner.easynotes.webclient;

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

import static junit.framework.Assert.assertEquals;

import de.huebner.easynotes.UnitTestConstants;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;

public class NotebookControllerTest {

	private NotesServiceImpl notesService;
	
	private NotebookController cardController;

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
		cardController = new NotebookController();
		cardController.setNotesServiceImpl(notesService);
	}
	
	/**
	 * Simulates adding a new notebook and saving it via controller.
	 */
	@Test
	public void shouldAddNewNotebookAndSave() {
		String result = cardController.addNewNotebook();
		assertEquals("editNotebook.xhtml", result);
		
		Notebook notebook = cardController.getNotebook();
		notebook.setTitle("Hello");
		
		tx.begin();
		result = cardController.updateNotebook();
		assertEquals("success", result);
		tx.commit();
		
		List<Notebook> notebooks = cardController.getNotebookList();
		assertEquals(1, notebooks.size());
	}
}
