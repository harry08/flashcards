package de.huebner.easynotes.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;

public class CardMapper {

	/**
	 * Maps a given card entity to a newly created card transport object.
	 * 
	 * @param card
	 *            card entity to map
	 * @param mapCategories
	 *            Flag to indicate if notebook categories should be mapped.
	 * @return Card transport object
	 */
	public CardTO mapEntityToTO(Card card) {
		CardTO cardTO = new CardTO();
		cardTO.setId(card.getId());
		cardTO.setFrontText(card.getFrontText());
		cardTO.setBackText(card.getBackText());
		cardTO.setText(card.getText());

		return cardTO;
	}

	/**
	 * Maps a list of card entities to a newly created CardTO list.
	 * 
	 * @param cards
	 *            cards to map
	 * @return List with Card transport objects
	 */
	public List<CardTO> mapEntityListToTOList(List<Card> cards) {
		List<CardTO> cardTOs = new ArrayList<CardTO>(cards.size());
		Iterator<Card> iterator = cards.iterator();
		while (iterator.hasNext()) {
			Card currentCard = iterator.next();

			CardTO cardTO = mapEntityToTO(currentCard);
			cardTOs.add(cardTO);
		}

		return cardTOs;
	}
}
