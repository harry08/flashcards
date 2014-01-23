package de.huebner.easynotes.webclient;

import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;

/**
 * Container holding a list of cards. Provides methods to advance through this list
 * in slide mode. 
 */
public class CardSlideContainer {
	private static final int CARD_SIDE_FRONT = 1;
	private static final int CARD_SIDE_BACK = 2;
	private static final int CARD_SIDE_BOTH = 3;

	private List<Card> cardList;
	
	/**
	 * Title with information about the current card.
	 */
	private String slideTitle;

	/**
	 * Header field
	 */
	private String cardHeader;

	/**
	 * Subheader field
	 */
	private String cardSubheader;

	/**
	 * Text field
	 */
	private String cardText;

	/**
	 * Is true, when the subheader for the card should be shown.
	 */
	private boolean subheaderShown;

	/**
	 * Is true, when the text for the card should be shown.
	 */
	private boolean textShown;

	/**
	 * Currently active card
	 */
	private Card currentCard;

	/**
	 * nr of the currently displayed card
	 */
	private int currentSlideNr;

	/**
	 * indicator which side of the currently displayed card is shown. front,
	 * back or both
	 */
	private int currentCardSide;
	
	/**
	 * Flag to control if the last answer should be shown
	 */
	private boolean showLastAnswer;

	/**
	 * Creates the container with a given list of cards and the card to display
	 * 
	 * @param cardList
	 * @param cardToShow
	 */
	public CardSlideContainer(List<Card> cardList, Card cardToShow) {
		this.cardList = cardList;
		this.currentCard = cardToShow;

		currentSlideNr = cardList.indexOf(currentCard);
		currentCardSide = CARD_SIDE_FRONT;
		showLastAnswer = false;

		generateSlideInformation();
	}
	
	/**
	 * Creates the container with a given list of cards. The first card of the
	 * list should be selected.
	 * 
	 * @param cardList
	 */
	public CardSlideContainer(List<Card> cardList, boolean showLastAnswer) {
		this.cardList = cardList;
		this.showLastAnswer = showLastAnswer;

		currentSlideNr = 0;
		currentCard = this.cardList.get(currentSlideNr);
		currentCardSide = CARD_SIDE_FRONT;

		generateSlideInformation();
	}

	/**
	 * Navigates to the previous card.
	 */
	public void previousCard() {
		if (currentSlideNr > 0) {
			currentSlideNr--;
			currentCard = cardList.get(currentSlideNr);
			if (currentCardSide != CARD_SIDE_BOTH) {
				currentCardSide = CARD_SIDE_FRONT;
			}
		}

		generateSlideInformation();
	}

	/**
	 * Navigates to the next card of the cardlist.
	 */
	public void nextCard() {
		if (currentSlideNr < cardList.size() - 1) {
			currentSlideNr++;
			currentCard = cardList.get(currentSlideNr);
			if (currentCardSide != CARD_SIDE_BOTH) {
				currentCardSide = CARD_SIDE_FRONT;
			}
		}

		generateSlideInformation();
	}
	
	/**
	 * Checks whether it is possible to navigate to the next card or not.
	 * 
	 * @return true, if more cards are available.
	 */
	public boolean hasNextCard() {
		return currentSlideNr < cardList.size() - 1;
	}

	/**
	 * Checks whether it is possible to navigate to the previous card or not.
	 * 
	 * @return true, if there is a card before the current card.
	 */
	public boolean hasPreviousCard() {
		return currentSlideNr > 0;
	}
	
	/**
	 * Flips the side of the currently displayed card.
	 * 
	 * @return null to show the current page.
	 */
	public void flipCard() {
		if (currentCardSide == CARD_SIDE_FRONT) {
			currentCardSide = CARD_SIDE_BACK;
		} else {
			currentCardSide = CARD_SIDE_FRONT;
		}

		generateSlideInformation();
	}
	
	public Card getCurrentCard() {
		return currentCard;
	}

	private void generateSlideInformation() {
		int size = cardList.size();
		int nr = currentSlideNr + 1;

		slideTitle = "Card " + nr + " of " + size;
		
		String frontText = currentCard.getFrontText();
		String backText = currentCard.getBackText();
		if (backText == null) {
			backText = "";
		}
		String text = currentCard.getText();
		if (text == null) {
			text = "";
		}

		if (currentCardSide == CARD_SIDE_FRONT) {
			// Display the fronttext of the card
			cardHeader = frontText;
			cardSubheader = "";
			cardText = "";

			subheaderShown = false;
			textShown = false;

		} else if (currentCardSide == CARD_SIDE_BACK) {
			// Display the backtext and the description text of the card
			cardHeader = backText;
			cardSubheader = "";
			cardText = text;

			subheaderShown = false;
			textShown = cardText.length() > 0;

		} else {
			// Show all information from the card
			cardHeader = frontText;
			cardSubheader = backText;
			cardText = text;

			subheaderShown = cardSubheader.length() > 0;
			textShown = cardText.length() > 0;
		}
	}
	
	public List<Card> getCardList() {
		return cardList;
	}
	
	public String getSlideTitle() {
		return slideTitle;
	}

	public String getCardHeader() {
		return cardHeader;
	}

	public String getCardSubheader() {
		return cardSubheader;
	}

	public String getCardText() {
		return cardText;
	}

	public boolean getSubheaderShown() {
		return subheaderShown;
	}

	public boolean getTextShown() {
		return textShown;
	}
	
	public String getCardSide() {
		String sideInfo = "";
		if (currentCardSide == CARD_SIDE_FRONT) {
			sideInfo = "front";
		} else if (currentCardSide == CARD_SIDE_BACK) {
			sideInfo = "back";
		}
		
		return sideInfo;
	}
	
	public String getCardAnswer() {
		String answerInfo = "";
		if (showLastAnswer) {
			if (currentCard.isAnsweredCorrect()) {
				answerInfo = "answered correct";
			} else if (currentCard.isAnsweredWrong()) {
				answerInfo = "answered wrong";
			}
		}
		
		return answerInfo;
	}
}
