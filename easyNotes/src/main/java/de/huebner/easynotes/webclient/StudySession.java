package de.huebner.easynotes.webclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;
import de.huebner.easynotes.businesslogic.study.SessionStatistic;

/**
 * Represents a session to study cards.
 */
public class StudySession {

	private final static int PROCEDURE_LESSON_RANDOM = 1;
	private final static int PROCEDURE_LESSON = 2;
	private final static int PROCEDURE_GIVENLIST = 2;

	private List<Card> cardList;

	private int studyProcedure;
	
	int maxCards = 25;

	/**
	 * Indicator if the session is running. After commit of the session the
	 * session is finished.
	 */
	private boolean sessionFinished;
	
	private SessionStatistic sessionStatistic;
	
	private boolean moreCardsAvailable;

	private Notebook selectedNotebook;

	private NotesServiceImpl notesService;
	
	private boolean changed;

	public StudySession(NotesServiceImpl notesService, Notebook notebook) {
		this.notesService = notesService;
		studyProcedure = PROCEDURE_LESSON;
		selectedNotebook = notebook;

		retrieveNextCards();
		sessionFinished = false;
		moreCardsAvailable = false;
		changed = false;
	}
	
	public StudySession(NotesServiceImpl notesService, List<Card> cardList) {
		this.notesService = notesService;
		studyProcedure = PROCEDURE_GIVENLIST;
		selectedNotebook = null;
		
		this.cardList = cardList;
		sessionFinished = false;
		moreCardsAvailable = false;
		clearLastAnswer();
		changed = false;
	}

	/**
	 * Retrieves the next cards to learn and prepares them for learning.
	 */
	private void retrieveNextCards() {
		if (selectedNotebook != null) {
			cardList = notesService.getCardsForLesson(selectedNotebook, maxCards); 	
			
			if (studyProcedure == PROCEDURE_LESSON_RANDOM) {
	            // Shuffle cards list.
	            Collections.shuffle(cardList);
	        }
			
			clearLastAnswer();
		}
	}
	
	/**
	 * Resets the study info of all the cards in the list.
	 * For this the last answer is cleared and the studied flag is set to false.
	 */
	private void clearLastAnswer() {
		for (Card currentCard : cardList) {
			currentCard.resetCurrentStudyInfo();
		}
	}

	/**
	 * Clears the data of the active session. After this, a call to isInit
	 * returns false.
	 */
	public void cancelSession() {
		cardList = new ArrayList<Card>(0);
		sessionFinished = true;
		moreCardsAvailable = false;
		changed = false;
	}

	public List<Card> getCardList() {
		return cardList;
	}

	/**
	 * Returns if the session is init. i.e. the cards can be studied.
	 * 
	 * @return true, if the session is init. Otherwise false.
	 */
	public boolean isInit() {
		return (cardList != null && cardList.size() > 0 && !isSessionFinished());
	}

	public boolean isSessionFinished() {
		return sessionFinished;
	}

	public int nrOfCards() {
		return cardList.size();
	}

	/**
	 * Checks if cards has been stuedied.
	 * 
	 * @return true, if at least one of the Cards is answered.
	 */
	public boolean isChanged() {
		// return changed;
		for (Card currentCard : cardList) {
			if (currentCard.isStudied()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns whether or not the study session can select more Cards to learn
	 * to start another learn session with the parameters given in the
	 * constructor. More Cards can only be selected in the Study modes Lesson
	 * and Random.
	 * 
	 * @return true, if more cards are available. False otherwise.
	 */
	private boolean hasMoreCards() {
		if (studyProcedure == PROCEDURE_LESSON_RANDOM
			|| studyProcedure == PROCEDURE_LESSON) {

			List<Card> tempList = notesService
					.getCardsForLesson(selectedNotebook, 1);

			return tempList.size() >= 1;
		}
		
		return false;
	}

	/**
	 * Commits the current study session. This means, the learnprogress of the
	 * studied cards is calculated and saved.
	 * 
	 * @return true, if the session was initialized and data has been chaged.
	 *         Otherwise false.
	 */
	public boolean commitSession() {
		if (isInit() && isChanged()) {
			sessionStatistic = notesService.commitLearnSession(cardList);

			sessionFinished = true;
			moreCardsAvailable = hasMoreCards();

			return true;
		}

		return false;
	}
	
	/**
	 * Starts a new study session with the same parameters of the former
	 * session. Only possible if the current session is finished and more cards
	 * to learn are available.
	 */
	public void startOver() {
		if (!isInit()) {
			retrieveNextCards();
			sessionFinished = false;
			changed = false;
			moreCardsAvailable = false;
			sessionStatistic = null;			
		}
	}
	
	public void answeredCorrect(Card card) {
		if (card != null) {
			int maxAnswer = 9;
			if (card.getNrOfCorrect() < maxAnswer) {
				card.setAnsweredCorrect();
				card.setLastLearned(new Date());
				changed = true;
			}
		}
	}
	
	public void markAsLearned(Card card) {
		if (card != null) {
			int maxAnswer = 9;
			if (card.getNrOfCorrect() < maxAnswer) {
				card.markAsLearned(maxAnswer);
				card.setLastLearned(new Date());
				changed = true;
			}
		}
	}
	
	public void resetProgress(Card card) {
		if (card != null) {
			card.resetProgress();
			card.setLastLearned(null);
			changed = true;
		}
	}

	public void answeredWrong(Card card) {
		if (card != null) {
			card.setAnsweredWrong();
			card.setLastLearned(new Date());
			changed = true;
		}
	}
	
	public SessionStatistic getSessionStatistic() {
		return sessionStatistic;
	}
	
	public boolean getMoreCardsAvailable() {
		return moreCardsAvailable;
	}
}
