package de.huebner.easynotes.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;

/**
 * REST Service for Cards
 */
@Path("/cards")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Stateless
public class CardResource {
	
	@EJB
	private NotesServiceImpl notesServiceImpl;

	@Context
	UriInfo uriInfo;
	
	/**
	 * Returns the card with the given id.<br>
	 * Request: /rs/cards/151
	 */
	@GET
	@Path("{cardId}/")
	public CardTO getCard(@PathParam("cardId") long cardId) {
		Card foundCard = notesServiceImpl.getCard(cardId);
		if (foundCard != null) {
			CardMapper mapper = new CardMapper();
			CardTO cardTO = mapper.mapEntityToTO(foundCard);
			
			return cardTO;
		}
		
		return null;
	}
	
	/**
	 * Returns a list of all cards of a notebooks<br>
	 * Sample request:
	 * http://127.0.0.1:8080/easyNotes-1.0.0-SNAPSHOT/rs/cards/query?notebookId=151
	 */
	@GET
	@Path("/query")
	public List<CardTO> getCardsOfNotebook(
			@QueryParam("notebookId") long notebookId) {
		Notebook foundNotebook = notesServiceImpl.getNotebook(notebookId);
		if (foundNotebook != null) {
			List<Card> cards = notesServiceImpl.getCardsOfNotebook(foundNotebook);
			
			CardMapper mapper = new CardMapper();
			List<CardTO> cardTOList = mapper.mapEntityListToTOList(cards);

			return cardTOList;
		}

		return null;
	}
}
