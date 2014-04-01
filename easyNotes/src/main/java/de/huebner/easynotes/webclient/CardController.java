package de.huebner.easynotes.webclient;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;
import de.huebner.easynotes.common.CommonConstants;

/**
 * Controller to manage cards. This includes showing a list of cards for a
 * selected notebook and editing cards.
 */
@ManagedBean
@SessionScoped
public class CardController implements Serializable {

	private static final long serialVersionUID = -9123720022516582466L;

	/**
	 * The cards are selected for this notebook.
	 */
	private Notebook selectedNotebook;

	private Card card;

	@EJB
	private NotesServiceImpl notesServiceImpl;

	private List<Card> cardList;

	private String selectedFilterId = String.valueOf(-1);
	
	/**
	 * List with filter entries to select in the dropdown box
	 */
	private List<SelectableItem> filterSelectItems;

	/**
	 * Generated title of page
	 */
	private String pageTitle;

	private String nrOfCards;

	private String created;
	
	private String lastEdited;
	
	private String lastStudied;

	private String parentPage = "listNotebooks.xhtml";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public List<Card> getCardList() {
		if (cardList == null) {
			retrieveCards();
		}

		return cardList;
	}

	/**
	 * Selects the given notebook to display the cards of it. For this notebook
	 * the containing cards are selected, a few extra information is selected
	 * and the page with the cards of the notebook is shown.
	 * 
	 * @return Page to display the cards of the notebook.
	 */
	public String selectNotebook(Notebook notebook) {
		this.selectedNotebook = notebook;
		selectedFilterId = String.valueOf(-1);
		retrieveCards();

		return "listCards.xhmtl";
	}

	/**
	 * Retrieves the cards and publishes the attribute cardList.
	 */
	private void retrieveCards() {
		int filterId = Integer.valueOf(selectedFilterId);
		if (filterId == -1) {
			cardList = notesServiceImpl.getCardsOfNotebook(selectedNotebook);
		} else {
			cardList = notesServiceImpl.getCardsOfNotebook(selectedNotebook, filterId);
		}
		
		setNrOfCards(cardList.size());
		calculatetLastEdited();
	}

	private void calculatetLastEdited() {
		created = "";
		lastEdited = "";
		lastStudied = "";
		
		if (cardList.size() > 0) {
			Date newestEditDate = cardList.get(0).getModified();
			Date newestStudyDate = cardList.get(0).getLastLearned();
			if (newestStudyDate == null) {
				newestStudyDate = new Date(0);
			}
			for (int i = 1; i < cardList.size(); i++) {
				Date compareEditDate = cardList.get(i).getModified();
				if (newestEditDate.compareTo(compareEditDate) < 0) {
					newestEditDate = compareEditDate;
				}
				
				Date compareStudyDate = cardList.get(i).getLastLearned();
				if (compareStudyDate == null) {
					compareStudyDate = new Date(0);
				}
				if (newestStudyDate.compareTo(compareStudyDate) < 0) {
					newestStudyDate = compareStudyDate;
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			lastEdited = sdf.format(newestEditDate);
			
			if (newestStudyDate.getTime() > 0) {
				lastStudied = sdf.format(newestStudyDate);
			}			
		}
	}
	
	/**
	 * Returns a list with items to display in the filter dropdown
	 * element.
	 * 
	 * @return list with SelectableItems
	 */
	public List<SelectableItem> getFilterSelectItems() {
		if (filterSelectItems == null) {
			filterSelectItems = new ArrayList<SelectableItem>(7);
			
			filterSelectItems.add(new SelectableItem(CommonConstants.FILTER_NOT, "Not filtered"));
			filterSelectItems.add(new SelectableItem(CommonConstants.FILTER_ANSWER_WRONG, "Answered wrong"));
			filterSelectItems.add(new SelectableItem(CommonConstants.FILTER_ANSWER_CORRECT, "Answered correct"));
			filterSelectItems.add(new SelectableItem(CommonConstants.FILTER_RECENT_STUDY, "Recently studied"));
			filterSelectItems.add(new SelectableItem(CommonConstants.FILTER_LESSON, "Lesson"));
			filterSelectItems.add(new SelectableItem(CommonConstants.FILTER_RECENT_ADD, "Recently added"));
			filterSelectItems.add(new SelectableItem(CommonConstants.FILTER_RECENT_MODIFIED, "REcently modified"));
			
			// TODO new filter for learned
		}

		return filterSelectItems;
	}
	
	/**
	 * Change method for filter dropdown. The cardlist is updated.
	 * 
	 * @param e
	 *            event
	 */
	public void filterListChanged(ValueChangeEvent e) {
		selectedFilterId = e.getNewValue().toString();

		retrieveCards();
	}
	
	public String getFilter() {
		return selectedFilterId;
	}
	
	public void setFilter(String filter) {
		this.selectedFilterId = filter;
	}
	
	/**
	 * Increases the learn progress of every card in the given cardlist.
	 * 
	 * @return null. This results in the presentation of the same page
	 */
	public String incLearnProgress() {
		StudySession studySession = new StudySession(notesServiceImpl, cardList);
		if (studySession.isInit()) {
			for (Card currentCard : cardList) {
				studySession.answeredCorrect(currentCard);				
			}
			
			if (studySession.isChanged()) {				
				studySession.commitSession();
				
				// TODO Info message about how many cards has been changed.
			}
		}
		
		return null;
	}

	/**
	 * Edits the selected card.
	 * 
	 * @param card
	 *            card to edit
	 * @return Page to edit the card.
	 */
	public String editCard(Card card) {
		this.card = card;

		pageTitle = "Edit card";
		created = sdf.format(card.getCreated());
		lastEdited = sdf.format(card.getModified());
		
		return "editCard.xhtml";
	}

	/**
	 * Persists the edited card and refreshes the cardList. Returns success.
	 * Success is interpreted to show the cardlist page with the updated card
	 * list.
	 */
	public String updateCard() {
		card = notesServiceImpl.updateCard(card);
		retrieveCards();

		return "success";
	}

	public String cancelEdit() {
		return "cancel";
	}

	/**
	 * Creates a new card instance and returns the edit page to edit this card.
	 * 
	 * @return Page to edit the new card.
	 */
	public String addNewCard() {
		card = new Card();
		card.setNotebook(selectedNotebook);

		pageTitle = "Create a new card";

		return "editCard.xhtml";
	}

	/**
	 * Deletes the given card
	 * 
	 * @param card
	 *            card to delete
	 * @return null. This results in the presentation of the same page
	 */
	public String deleteCard(Card card) {
		notesServiceImpl.deleteCard(card);
		retrieveCards();

		return null;
	}

	public String showCardList(boolean refresh) {
		if (refresh) {
			retrieveCards();
		}

		return "listCards.xhtml";
	}

	public String openExportPage() {
		return "cardExport.xhtml";
	}

	public String backToNotebookList() {
		return parentPage;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public Notebook getSelectedNotebook() {
		return selectedNotebook;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public String getNrOfCards() {
		return nrOfCards;
	}

	public void setNrOfCards(int nrOfCards) {
		this.nrOfCards = "" + nrOfCards;
	}

	public String getCreated() {
		return created;
	}
	
	public String getLastEdited() {
		return lastEdited;
	}
	
	public String getLastStudied() {
		return lastStudied;
	}
	
	public String getLastLearnedValue(Card currentCard) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	    if (currentCard.getLastLearned() != null) {
	    	return sdf.format(currentCard.getLastLearned());
	    } 		
	    
	    return "";
	}
	
	public String getNextScheduledValue(Card currentCard) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	    if (currentCard.getNextScheduled() != null) {
	    	return sdf.format(currentCard.getNextScheduled());
	    } 		
	    
	    return "";
	}
}
