package de.huebner.easynotes.businesslogic.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.huebner.easynotes.businesslogic.NotesServiceBusinessException;
import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Category;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.io.CardExportData;
import de.huebner.easynotes.businesslogic.io.CardsExporter;
import de.huebner.easynotes.businesslogic.io.CardsImporter;
import de.huebner.easynotes.businesslogic.io.CardsImporterException;

/**
 * Facade for accessing the notes database. Includes business logic.
 */
@Stateless
public class NotesServiceImpl implements Serializable {

	private static final long serialVersionUID = -5780061057564128219L;

	@PersistenceContext(unitName = "NotesPersistenceUnit")
	private EntityManager entityManager;

	private void setAutomaticValues(Card card) {
		boolean insert = (card.getId() <= 0);

		Date modificationDate = new Date();
		if (insert) {
			card.setCreated(modificationDate);
		}
		card.setModified(modificationDate);
	}

	private void setAutomaticValues(Notebook notebook) {
		boolean insert = (notebook.getId() <= 0);

		Date modificationDate = new Date();
		if (insert) {
			notebook.setCreated(modificationDate);
		}
		notebook.setModified(modificationDate);
	}

	private void setAutomaticValues(Category category) {
		boolean insert = (category.getId() <= 0);

		Date modificationDate = new Date();
		if (insert) {
			category.setCreated(modificationDate);
		}
		category.setModified(modificationDate);
	}

	/**
	 * Returns a list of all notebooks.
	 * 
	 * @return list with all notebooks
	 */
	public List<Notebook> getAllNotebooks() {
		Query query = entityManager.createNamedQuery("Notebook.findAllNotebooks");

		// Query ausfuehren
		@SuppressWarnings("unchecked")
		List<Notebook> result = query.getResultList();

		return result;
	}

	/**
	 * Returns a list of all notebooks associated to the specified category
	 * 
	 * @param category
	 *          category to filter
	 * @return list with all notebooks associated to the specified category
	 */
	public List<Notebook> getAllNotebooks(Category category) {
		Query query = entityManager.createNamedQuery("Notebook.findNotebooksForCategory");
		query.setParameter("catid", category.getId());

		// Query ausfuehren
		@SuppressWarnings("unchecked")
		List<Notebook> result = query.getResultList();

		return result;
	}

	/**
	 * Returns a list of all categories
	 * 
	 * @return list with all categories
	 */
	public List<Category> getAllCategories() {
		Query query = entityManager.createNamedQuery("Category.findAllCategories");

		// Perform Query
		@SuppressWarnings("unchecked")
		List<Category> result = query.getResultList();

		return result;
	}

	/**
	 * Returns a list of all cards of a notebook.
	 * 
	 * @param notebook
	 *          notebook from which the cards should be returned
	 * @return list with cards
	 */
	public List<Card> getCardsOfNotebook(Notebook notebook) {
		Query query = entityManager.createNamedQuery("Card.findCardsOfNotebook");
		query.setParameter("notebook", notebook);

		// Perform Query
		@SuppressWarnings("unchecked")
		List<Card> result = query.getResultList();

		return result;
	}
	
	public Notebook getNotebook(long id) {
		Query query = entityManager.createNamedQuery("Notebook.findNotebookWithId");
		query.setParameter("id", id);
		
		// Perform Query
		@SuppressWarnings("unchecked")
		List<Notebook> result = query.getResultList();
		if (result.size() == 1) {
			return result.get(0);
		}
				
		return null;
	}

	/**
	 * Creates or updates the given card to the database
	 * 
	 * @param card
	 *          card to create or update
	 * @return updated Card object
	 */
	public Card updateCard(Card card) {
		setAutomaticValues(card);
		Card updatedCard = entityManager.merge(card);

		return updatedCard;
	}

	/**
	 * Creates or updates the given notebook to the database
	 * 
	 * @param notebook
	 *          notebook to create or update
	 * @return updated notebook object
	 */
	public Notebook updateNotebook(Notebook notebook) {
		setAutomaticValues(notebook);
		Notebook updatedNotebook = entityManager.merge(notebook);

		return updatedNotebook;
	}

	/**
	 * Creates or updates the given category to the database
	 * 
	 * @param category
	 *          category to create or update
	 * @return updated category object
	 */
	public Category updateCategory(Category category) {
		setAutomaticValues(category);
		Category updatedCategory = entityManager.merge(category);

		return updatedCategory;
	}
	
	/**
	 * Deletes the given category. If there are notebooks associated to this
	 * category, these associations first will be removed.
	 * 
	 * @param category
	 *          category to remove
	 * @return true, if the category could be removed.
	 */
	public boolean deleteCategory(Category category) {
		// Remove notebook associations
		List<Notebook> catNotebooks = getAllNotebooks(category);
		if (catNotebooks != null && catNotebooks.size() > 0) {
			for (Notebook currentNotebook : catNotebooks) {
				currentNotebook.removeCategoryFromNotebook(category);
				entityManager.merge(currentNotebook);
			}
		}
		
		entityManager.remove(category);

		return true;
	}

	/**
	 * Imports a list of cards from the given importString. The string is
	 * formatted as csv.
	 * 
	 * @param importString
	 *          String to import
	 * @param notebook
	 *          Notebook to import the cards
	 * @return nr of imported cards
	 */
	public int importCards(String importString, Notebook notebook)
			throws NotesServiceBusinessException {
		// Extract cards out of the input string
		CardsImporter cardsImporter = new CardsImporter();
		List<Card> cardList;
		try {
			cardList = cardsImporter.interpretInputString(importString);
		} catch (CardsImporterException cie) {
			cie.printStackTrace();
			throw new NotesServiceBusinessException("Error reading the input data: " + cie.getMessage());
		}

		// Bulk insert all cards
		int i = 0;
		try {
			for (Card currentCard : cardList) {
				setAutomaticValues(currentCard);
				currentCard.setNotebook(notebook);
				entityManager.persist(currentCard);

				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().setRollbackOnly();
			throw new NotesServiceBusinessException("Error while importing cards");
		}

		return i;
	}

	/**
	 * Exports the cards of the given notebook to a string.
	 * 
	 * @param notebook
	 *          Notebook from which the cards are exported
	 * @param exportType
	 * @param wordDelimiter
	 *          delimiter for words
	 * @param recordDelimiter
	 *          delimiter for a whole record
	 * @return Exported data. Contains String and nr of records in this string.
	 */
	public CardExportData exportCards(Notebook notebook, int exportType, String wordDelimiter,
			String recordDelimiter) {
		CardsExporter cardsExporter = new CardsExporter();
		List<Card> cards = getCardsOfNotebook(notebook);

		CardExportData exportData = cardsExporter.generateExportString(cards, exportType,
				wordDelimiter, recordDelimiter);

		return exportData;
	}

	/**
	 * Setzt den {@link EntityManager}, der von dieser Instanz verwendet wird
	 * (nur fuer Tests ausserhalb eines Servers).
	 * 
	 * @param entityManager
	 *            EntityManager to use
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Liefert den {@link EntityManager}, der von dieser Instanz verwendet wird
	 * (nur fuer Tests ausserhalb eines Servers).
	 * 
	 * @return used EntityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
