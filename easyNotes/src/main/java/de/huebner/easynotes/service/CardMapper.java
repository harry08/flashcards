package de.huebner.easynotes.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.common.data.CardEntry;


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
	public CardEntry mapEntityToTO(Card card) {
		CardEntry cardEntry = new CardEntry();
		cardEntry.setId(card.getId());
		cardEntry.setFrontText(card.getFrontText());
		cardEntry.setBackText(card.getBackText());
		cardEntry.setText(card.getText());

		return cardEntry;
	}

	/**
	 * Maps a list of card entities to a newly created CardEntry list.
	 * 
	 * @param cards
	 *            cards to map
	 * @return List with Card transport objects
	 */
	public List<CardEntry> mapEntityListToTOList(List<Card> cards) {
		List<CardEntry> cardEntries = new ArrayList<CardEntry>(cards.size());
		Iterator<Card> iterator = cards.iterator();
		while (iterator.hasNext()) {
			Card currentCard = iterator.next();

			CardEntry cardEntry = mapEntityToTO(currentCard);
			cardEntries.add(cardEntry);
		}

		return cardEntries;
	}
	
	/**
	 * Maps a given cardEntry object to a Card entity object.
	 * Only client changeable data is being mapped.
	 * 
	 * @param cardEntry
	 *            object to map
	 * @param card
	 *            target object
	 */
	public void mapTOToEntity(CardEntry cardEntry, Card card) {
		card.setFrontText(cardEntry.getFrontText());
		card.setFrontText(cardEntry.getBackText());
		card.setFrontText(cardEntry.getText());
	}
}
