package de.huebner.easynotes.webclient;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;

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

	/**
	 * Generated title of page
	 */
	private String pageTitle;

	private String nrOfCards;

	private String lastEdited;

	private String parentPage = "listNotebooks.xhtml";

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
		retrieveCards();

		return "listCards.xhmtl";
	}

	/**
	 * Refreshes the attributes of this Controller
	 */
	private void retrieveCards() {
		cardList = notesServiceImpl.getCardsOfNotebook(selectedNotebook);
		setNrOfCards(cardList.size());
		calculatetLastEdited();
	}

	private void calculatetLastEdited() {
		if (cardList.size() > 0) {
			Date newestDate = cardList.get(0).getModified();
			for (int i = 1; i < cardList.size(); i++) {
				Date compareDate = cardList.get(i).getModified();
				if (newestDate.compareTo(compareDate) < 0) {
					newestDate = compareDate;
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			lastEdited = sdf.format(newestDate);
		} else {
			lastEdited = "";
		}
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

		return "editCard.xhtml";
	}

//	/**
//	 * Shows the selected card in a slide mode.
//	 * 
//	 * @param card
//	 *            card to show
//	 * @return Page to show a card in a slide mode
//	 */
//	public String showCard(Card card) {
//		cardSlideContainer = new CardSlideContainer(cardList, card);
//
//		return "cardSlide.xhtml";
//	}

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

	public String getLastEdited() {
		return lastEdited;
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
