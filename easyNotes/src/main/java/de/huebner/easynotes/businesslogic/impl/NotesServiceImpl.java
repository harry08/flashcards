package de.huebner.easynotes.businesslogic.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.huebner.easynotes.businesslogic.NotesServiceBusinessException;
import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Category;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.io.CardCreator;
import de.huebner.easynotes.businesslogic.io.CardExportData;
import de.huebner.easynotes.businesslogic.io.CardsExporter;
import de.huebner.easynotes.businesslogic.io.CsvImporter;
import de.huebner.easynotes.businesslogic.io.CardsImporterException;
import de.huebner.easynotes.businesslogic.io.ImportObjectCreator;
import de.huebner.easynotes.businesslogic.study.LearnProgressCalculator;
import de.huebner.easynotes.businesslogic.study.SessionStatistic;
import de.huebner.easynotes.common.CommonConstants;

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
			if (card.getNextScheduled() == null) {
				card.setNextScheduled(modificationDate);
			}
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

		// Perform query
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

		// Perform query
		@SuppressWarnings("unchecked")
		List<Notebook> result = query.getResultList();

		return result;
	}
	
	/**
	 * Returns a list of all notebooks that are not associated to a category
	 * 
	 * @return list with all notebooks associated to no category
	 */
	public List<Notebook> getAllNotebooksWithNoCategory() {
		Query query = entityManager.createNativeQuery(
				Notebook.NB_WITHOUT_CATEGORY_QUERY, Notebook.class);

		// Perform query
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
	
	public Card getCard(long id) {
		Query query = entityManager.createNamedQuery("Card.findCardWithId");
		query.setParameter("id", id);
		
		// Perform Query
		@SuppressWarnings("unchecked")
		List<Card> result = query.getResultList();
		if (result.size() == 1) {
			return result.get(0);
		}
				
		return null;
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
	
	/**
	 * Returns a filtered list of all cards of a notebook.
	 * 
	 * @param notebook
	 *            notebook from which the cards should be returned
	 * @param filter
	 *            filter to use to select the cards
	 * @return list with cards
	 */
	public List<Card> getCardsOfNotebook(Notebook notebook, int filter) {
		Calendar recentCal = new GregorianCalendar();
		recentCal.add(Calendar.DATE, -2);
		
		Query query = null;
		
		if (filter == CommonConstants.FILTER_ANSWER_WRONG || filter == CommonConstants.FILTER_ANSWER_CORRECT) {
			query = entityManager.createNamedQuery("Card.findAnsweredCardsOfNotebook");
			query.setParameter("notebook", notebook);
			if (filter == CommonConstants.FILTER_ANSWER_WRONG) {
				query.setParameter("answer", Card.ANSWER_WRONG);
			} else {
				query.setParameter("answer", Card.ANSWER_CORRECT);
			}
		} else if (filter == CommonConstants.FILTER_LESSON) {
			query = entityManager.createNamedQuery("Card.findCardsForLesson");
			query.setParameter("notebook", notebook);
			query.setParameter("nextScheduled", new Date());
		} else if (filter == CommonConstants.FILTER_RECENT_ADD) {
			query = entityManager.createNamedQuery("Card.findAnsweredCardsOfNotebookRecentAdded");
			query.setParameter("notebook", notebook);
			query.setParameter("created", recentCal.getTime());
		} else if (filter == CommonConstants.FILTER_RECENT_MODIFIED) {
			query = entityManager.createNamedQuery("Card.findAnsweredCardsOfNotebookRecentModified");
			query.setParameter("notebook", notebook);
			query.setParameter("modified", recentCal.getTime());
		} else if (filter == CommonConstants.FILTER_RECENT_STUDY) {
			query = entityManager.createNamedQuery("Card.findCardsOfNotebookRecentStudied");
			query.setParameter("notebook", notebook);
			query.setParameter("lastLearned", recentCal.getTime());
		} else {
			// not filtered
			query = entityManager.createNamedQuery("Card.findCardsOfNotebook");
			query.setParameter("notebook", notebook);
		}

		// Perform Query
		@SuppressWarnings("unchecked")
		List<Card> result = query.getResultList();

		return result;
	}
	
	public List<Card> getCardsForLesson(Notebook notebook, int maxCards) {
		Query query = entityManager.createNamedQuery("Card.findCardsForLesson");
		query.setParameter("notebook", notebook);
		query.setParameter("nextScheduled", new Date());
		
		// Perform Query
		@SuppressWarnings("unchecked")
		List<Card> tempList = query.getResultList();
		
		int requiredSize = tempList.size();
		if (requiredSize > maxCards) {
			requiredSize = maxCards;
		}
		List<Card> result = new ArrayList<Card>(requiredSize);
		int i = 0;
		while (i < requiredSize) {
			result.add(tempList.get(i));
			i++;
		}

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
	 * Commits the results of the learned cards. <br>
	 * This includes calculating the learn progress of each card and committing
	 * this information to the database.
	 * 
	 * @param studiedCards
	 *            list with learned cards.
	 * @return Statistic record
	 */
	public SessionStatistic commitLearnSession(List<Card> studiedCards) {
		SessionStatistic stat = new SessionStatistic();
		stat.setNrOfCards(studiedCards.size());
		LearnProgressCalculator calculator = new LearnProgressCalculator();

		for (Card currentCard : studiedCards) {
			//if (currentCard.isAnswered()) {
			if (currentCard.isStudied()) {
				// Calculate learn progress
				LearnProgressCalculator.LearnProgress progress = calculator
						.calculate(currentCard.getLastLearned(),
								currentCard.getAnswer(),
								currentCard.getNrOfCorrect(),
								currentCard.getNrOfWrong());

				currentCard.setNextScheduled(progress.getNextScheduled());
				currentCard.setCompartment(progress.getCompartment());

				stat.incNrOfLearned();
			} else {
				stat.incNrOfNotAnswered();
			}

			if (currentCard.isAnsweredCorrect()) {
				stat.incNrOfCorrect();
			} else if (currentCard.isAnsweredWrong()) {
				stat.incNrOfWrong();
			}
		}

		for (Card currentCard : studiedCards) {
			entityManager.merge(currentCard);
		}

		return stat;
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
	 * Deletes the given card. 
	 * 
	 * @param card
	 *          card to delete
	 * @return true, if the card could be removed.
	 */
	public boolean deleteCard(Card card) {
		card = entityManager.merge(card);
		entityManager.remove(card);

		return true;
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
	 * Deletes the given notebook. If there are cards associated to this
	 * notebook, these cards first will be deleted
	 * 
	 * @param notebook
	 *            notebook to delete
	 */
	public boolean deleteNotebook(Notebook notebook) {
		// notebook might be detached, so merge it before.
		Notebook notebookToDelete = entityManager.merge(notebook);

		// Delete contained cards
		List<Card> nbCards = getCardsOfNotebook(notebookToDelete);
		if (nbCards != null && nbCards.size() > 0) {
			for (Card currentCard : nbCards) {
				entityManager.remove(currentCard);
			}
		}

		entityManager.remove(notebookToDelete);

		return true;
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
	 *            category to remove
	 * @return true, if the category could be removed.
	 */
	public boolean deleteCategory(Category category) {
		// category might be detached, so merge it before.
		Category categoryToDelete = entityManager.merge(category);

		// Remove notebook associations
		List<Notebook> catNotebooks = getAllNotebooks(categoryToDelete);
		if (catNotebooks != null && catNotebooks.size() > 0) {
			for (Notebook currentNotebook : catNotebooks) {
				currentNotebook.removeCategoryFromNotebook(categoryToDelete);
				entityManager.merge(currentNotebook);
			}
		}

		entityManager.remove(categoryToDelete);

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
		CsvImporter cardsImporter = new CsvImporter();
		ImportObjectCreator cardCreator = new CardCreator();
		cardsImporter.setObjectCreator(cardCreator);

		List<Card> cardList;
		try {
			cardList = cardsImporter.interpretInputString(importString);
		} catch (CardsImporterException cie) {
			cie.printStackTrace();
			int errorCode = NotesServiceBusinessException.ERROR_IMPORT_GENERAL;
			String detail1 = "";
			String detail2 = "";
			if (cie.getErrorCode() == CardsImporterException.UNEXPECTED_HEADER) {
				errorCode = NotesServiceBusinessException.ERROR_IMPORT_WRONG_HEADER;
				detail1 = cie.getValue();
			} else if (cie.getErrorCode() == CardsImporterException.UNEXPECTED_FIELD) {
				errorCode = NotesServiceBusinessException.ERROR_IMPORT_UNEXPECTED_FIELD;
				detail1 = cie.getField();
				detail2 = cie.getValue();
			} else if (cie.getErrorCode() == CardsImporterException.FORMAT_ERROR) {
				errorCode = NotesServiceBusinessException.ERROR_IMPORT_WRONG_FORMAT;
				detail1 = cie.getField();
				detail2 = cie.getValue();
			} else if (cie.getErrorCode() == CardsImporterException.MORE_FIELDS) {
				errorCode = NotesServiceBusinessException.ERROR_IMPORT_MORE_FIELDS_THAN_EXPECTED;
				detail1 = cie.getValue();
			}

			throw new NotesServiceBusinessException(
					"Error reading the input data: " + cie.getMessage(),
					errorCode, detail1, detail2);
		}

		// Bulk insert all cards
		int i = 0;
		try {
			for (Card currentCard : cardList) {
				if (currentCard.getCreated() == null) {
					// No creationdate was provided with input data. 
					// Set new values
					Date insertionDate = new Date();
					currentCard.setCreated(insertionDate);
					currentCard.setModified(insertionDate);
					currentCard.setNextScheduled(insertionDate);				
				}
				currentCard.setNotebook(notebook);
				entityManager.persist(currentCard);

				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotesServiceBusinessException("Error while importing cards.");
		}

		return i;
	}

	/**
	 * Exports the cards of the given notebook to a string.
	 * 
	 * @param notebook
	 *            Notebook from which the cards are exported
	 * @param exportType
	 * @param wordDelimiter
	 *            delimiter for words
	 * @param recordDelimiter
	 *            delimiter for a whole record
	 * @param quoteDelimiter
	 *            should be set true for exporting csv files. Words having
	 *            delimiter symbols in their text will be double quoted in the
	 *            export string.
	 * @param containsHeader
	 *            should be true if a header line should be included in the
	 *            export string.
	 * @return Exported data. Contains String and nr of records in this string.
	 */
	public CardExportData exportCards(Notebook notebook, int exportType,
			String wordDelimiter, String recordDelimiter, boolean quoteDelimiter, boolean containsHeader) {
		CardsExporter cardsExporter = new CardsExporter();
		List<Card> cards = getCardsOfNotebook(notebook);

		CardExportData exportData = cardsExporter.generateExportString(cards,
				exportType, wordDelimiter, recordDelimiter, quoteDelimiter, containsHeader);

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
