package de.huebner.easynotes.webclient;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;

/**
 * Controller to show cards in slide mode. Uses CardSlideContainer to navigate
 * through the card list.
 */
@ManagedBean
@SessionScoped
public class CardSlideController {
	
	private static final int SLIDE_MODE_SHOW = 1;
	private static final int SLIDE_MODE_STUDY = 2;

	/**
	 * The cards are selected for this notebook.
	 */
	private Notebook selectedNotebook;
	
	/**
	 * Indicator to show that that last card of the list has been reached.
	 * Only used in study mode to show an appropriate information.
	 */
	private boolean reachedLastCard;

	private CardSlideContainer cardSlideContainer;
	
	private StudySession studySession;
	
	/**
	 * Indicator to show that cards has been studied.
	 * Can be used to refresh the cardlist to show the new progress.
	 */
	private boolean cardsStudied;

	@ManagedProperty(value="#{cardController}")
	private CardController cardController;
	
	@EJB
	private NotesServiceImpl notesServiceImpl;
	
	/**
	 * mode how the cards are shown. In slide mode or in study mode.
	 */
	private int cardSlideMode;
	
	/**
	 * error message to be shown on the sessionError page.
	 */
	private String errorMessage;

	/**
	 * Opens the slide page for the selected notebook and card
	 * 
	 * @return slide page
	 */
	public String openSlidePage(Notebook notebook, Card card) {
		cardSlideMode = SLIDE_MODE_SHOW;
		this.selectedNotebook = notebook;
		List<Card> cardList = cardController.getCardList();
		
		cardSlideContainer = new CardSlideContainer(cardList, card);
		
		cardsStudied = false;
		
		return "cardSlide.xhmtl";
	}
	
	/**
	 * Navigates back to the card list.
	 * 
	 * @return cardlist page
	 */
	public String backToCardList() {
		if (studySession != null && studySession.isInit()) {
			studySession.cancelSession();
		}
		return cardController.showCardList(cardsStudied);
	}
	
	/**
	 * Navigates back to the notebook list.
	 * 
	 * @return notebooklist page
	 */
	public String backToNotebookList() {
		if (studySession != null && studySession.isInit()) {
			studySession.cancelSession();
		}
		return "listNotebooks.xhtml";
	}
	
	/**
	 * Starts a study session with the cards to study according to the lesson of
	 * the selected notebook.
	 * 
	 * @return Page to study a card. This page shows the card in slide mode.
	 */
	public String studyLesson(Notebook notebook) {
		cardSlideMode = SLIDE_MODE_STUDY;
		this.selectedNotebook = notebook;
		reachedLastCard = false;
		cardsStudied = false;
		
		studySession = new StudySession(notesServiceImpl, selectedNotebook);
		if (studySession.isInit()) {
			cardSlideContainer = new CardSlideContainer(studySession.getCardList(), true);

			return "cardSlide.xhtml";
		} else {
			errorMessage = "A new study session could not be initialized. No cards available to study.";

			return "sessionError.xhtml";
		}		
	}

	/**
	 * Called in slide mode. Shows the previous card of the cardlist.
	 * 
	 * @return null to show the current page.
	 */
	public String previousCard() {
		cardSlideContainer.previousCard();

		return null;
	}

	/**
	 * Called in slide mode. Shows the next card of the cardlist.
	 * 
	 * @return null to show the current page.
	 */
	public String nextCard() {
		cardSlideContainer.nextCard();

		return null;
	}

	/**
	 * Flips the side of the currently displayed card.
	 * 
	 * @return null to show the current page.
	 */
	public String flipCard() {
		cardSlideContainer.flipCard();

		return null;
	}

	/**
	 * Sets the answer of the current card to correct. The slide advances to the
	 * next card if more cards are available. Otherwise an indicator is shown to
	 * stop the current learn session.
	 * 
	 * @return null to show the current page.
	 */
	public String answeredCorrect() {
		if (!cardSlideContainer.getCurrentCard().isAnswered()) {
			studySession.answeredCorrect(cardSlideContainer.getCurrentCard());

			if (cardSlideContainer.hasNextCard()) {
				// Show next card on same page
				cardSlideContainer.nextCard();

				return null;
			} else {
				// Stay on current card and show indicator
				reachedLastCard = true;

				return null;
			}
		}
		
		return null;
	}

	/**
	 * Sets the answer of the current card to wrong. The slide advances to the
	 * next card if more cards are available. Otherwise an indicator is shown to
	 * stop the current learn session.
	 * 
	 * @return null to show the current page.
	 */
	public String answeredWrong() {
		if (!cardSlideContainer.getCurrentCard().isAnswered()) {
			studySession.answeredWrong(cardSlideContainer.getCurrentCard());

			if (cardSlideContainer.hasNextCard()) {
				// Show next card on same page
				cardSlideContainer.nextCard();

				return null;
			} else {
				// Stay on current card and show indicator
				reachedLastCard = true;

				return null;
			}
		}

		return null;
	}

	/**
	 * Skips the current card without answering it. The slide advances to the
	 * next card if more cards are available. Otherwise an indicator is shown to
	 * stop the current learn session.
	 * 
	 * @return null to show the current page.
	 */
	public String skipCard() {
		if (cardSlideContainer.hasNextCard()) {
			// Show next card on same page
			cardSlideContainer.nextCard();

			return null;
		} else {
			// Stay on current card and show indicator
			reachedLastCard = true;

			return null;
		}
	}
	
	/**
	 * Stops the current learn session calculates the learnprogress and commits the information to the database.
	 * 
	 * @return null to show the current page.
	 */
	public String stopSession() {
		if (studySession.isInit() && studySession.isChanged()) {
			cardsStudied = true;
			
			studySession.commitSession();
			
			return "sessionSummary.xhtml";
		}	
		
		return null;
	}
	
	/**
	 * Restarts a study session
	 * 
	 * @return Page to study a card.
	 */
	public String startOver() {
		studySession.startOver();

		if (studySession.isInit()) {
			cardSlideContainer = new CardSlideContainer(
					studySession.getCardList(), true);
			reachedLastCard = false;
			
			return "cardSlide.xhtml";
		} else {
			errorMessage = "A new study session could not be initialized. No cards available to study.";

			return "sessionError.xhtml";
		}
	}
	
	/**
	 * Marks the current selected card as learned. 
	 * 
	 * @return Page to show the card 
	 */
	public String markAsLearned() {
		Card currentCard = cardSlideContainer.getCurrentCard();
		List<Card> tmpCardList = new ArrayList<Card>(1);
		tmpCardList.add(currentCard);
		
		StudySession tmpStudySession = new StudySession(notesServiceImpl, tmpCardList);
		if (tmpStudySession.isInit()) {
			tmpStudySession.markAsLearned(currentCard);
			
			if (tmpStudySession.isChanged()) {				
				tmpStudySession.commitSession();
				
				// get the changed card and replace it with the old one in the cardList.
				Card updatedCard = notesServiceImpl.getCard(currentCard.getId());
				cardSlideContainer.replaceCurrentCard(updatedCard);
				
				FacesMessage info = new FacesMessage(FacesMessage.SEVERITY_INFO, "Card marked as learned", null);
				FacesContext.getCurrentInstance().addMessage(null, info);
			}
		}

		return null;
	}
	
	public boolean getAllowMarkLearned() {
		return cardSlideMode == SLIDE_MODE_SHOW;
	} 
	
	public String resetProgress() {
		Card currentCard = cardSlideContainer.getCurrentCard();
		List<Card> tmpCardList = new ArrayList<Card>(1);
		tmpCardList.add(currentCard);
		
		StudySession tmpStudySession = new StudySession(notesServiceImpl, tmpCardList);
		if (tmpStudySession.isInit()) {
			tmpStudySession.resetProgress(currentCard);
			
			if (tmpStudySession.isChanged()) {				
				tmpStudySession.commitSession();
				
				// get the changed card and replace it with the old one in the cardList.
				Card updatedCard = notesServiceImpl.getCard(currentCard.getId());
				cardSlideContainer.replaceCurrentCard(updatedCard);
				
				FacesMessage info = new FacesMessage(FacesMessage.SEVERITY_INFO, "Card progress reset", null);
				FacesContext.getCurrentInstance().addMessage(null, info);
			}
		}
		
		return null;
	}
	
	public boolean getAllowResetProgress() {
		return cardSlideMode == SLIDE_MODE_SHOW;
	} 

	public CardSlideContainer getCardSlideContainer() {
		return cardSlideContainer;
	}
	
	public Notebook getSelectedNotebook() {
		return selectedNotebook;
	}
	
	public String getPageTitle() {
		return cardSlideContainer.getSlideTitle();
	}
	
	public CardController getCardController() {
		return cardController;
	}

	public void setCardController(CardController cardController) {
		this.cardController = cardController;
	}
	
	/**
	 * Used to control the visibility of elements that are only shown in study
	 * mode.
	 * 
	 * @return true, if the page is shown in study mode.
	 */
	public boolean getIsStudyMode() {
		return cardSlideMode == SLIDE_MODE_STUDY;
	}
	
	/**
	 * Used to control the visibility of answer buttons.
	 * 
	 * @return true, if the page is shown in study mode and the current card is not yet answered.
	 */
	public boolean getCanAnswer() {
		return getIsStudyMode() && !cardSlideContainer.getCurrentCard().isAnswered();
	}
	
	/**
	 * Used to control the disable property of previous button
	 * 
	 * @return true, if no previous card is available according to the current
	 *         position
	 */
	public boolean getHasNoPreviousCard() {
		return !cardSlideContainer.hasPreviousCard();
	}

	/**
	 * Used to control the disable property of next button
	 * 
	 * @return true, if no next card is available according to the current
	 *         position
	 */
	public boolean getHasNoNextCard() {
		return !cardSlideContainer.hasNextCard();
	}
	
	public boolean getShowAllCardsStudied() {
		return reachedLastCard && cardSlideMode == SLIDE_MODE_STUDY;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public StudySession getStudySession() {
		return studySession;
	}
}
