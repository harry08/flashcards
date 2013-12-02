package de.huebner.easynotes.businesslogic;

import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Notebook;

/**
 * Fassade fuer den Zugriff auf die Notes-Tabellen.
 */
public interface NotesService {

	public List<Notebook> getAllNotebooks();

	public List<Card> getCardsOfNotebook(Notebook notebook);

	/**
	 * Creates or updates the given card to the database
	 * 
	 * @param card
	 *          card to create or update
	 * @return updated Card object
	 */
	public Card updateCard(Card card);

	/**
	 * Creates or updates the given notebook to the database
	 * 
	 * @param notebook
	 *          notebook to create or update
	 * @return updated notebook object
	 */
	public Notebook updateNotebook(Notebook notebook);

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
	public int importCards(String importString, Notebook notebook) throws NotesServiceBusinessException;
}
