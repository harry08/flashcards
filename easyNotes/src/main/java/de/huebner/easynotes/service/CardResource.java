package de.huebner.easynotes.service;

import java.net.URI;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import de.huebner.easynotes.businesslogic.data.Card;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;
import de.huebner.easynotes.common.data.CardEntry;

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
	public CardEntry getCard(@PathParam("cardId") long cardId) {
		Card foundCard = notesServiceImpl.getCard(cardId);
		if (foundCard != null) {
			CardMapper mapper = new CardMapper();
			CardEntry cardEntry = mapper.mapEntityToTO(foundCard);
			
			return cardEntry;
		} else {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}	
	}
	
	/**
	 * Returns a list of all cards of a notebooks<br>
	 * Sample request:
	 * http://127.0.0.1:8080/easyNotes-1.0.0-SNAPSHOT/rs/cards/query?notebookId=151
	 */
	@GET
	@Path("/query")
	public List<CardEntry> getCardsOfNotebook(
			@QueryParam("notebookId") long notebookId) {
		Notebook foundNotebook = notesServiceImpl.getNotebook(notebookId);
		if (foundNotebook != null) {
			List<Card> cards = notesServiceImpl.getCardsOfNotebook(foundNotebook);
			
			CardMapper mapper = new CardMapper();
			List<CardEntry> cardEntryList = mapper.mapEntityListToTOList(cards);

			return cardEntryList;
		}

		return null;
	}
	
	/**
	 * Creates a new card with the given data.
	 * 
	 * @param cardJaxb
	 *            card data
	 * @return Response with the URI of the newly created card
	 */
	@POST
	public Response createNewCard(JAXBElement<CardEntry> cardJaxb) {
		CardEntry cardEntry = cardJaxb.getValue();
		
		CardMapper mapper = new CardMapper();
		Card card = new Card();
		mapper.mapTOToEntity(cardEntry, card);
		card = notesServiceImpl.updateCard(card);

		URI cardUri = uriInfo.getAbsolutePathBuilder()
				.path(String.valueOf(card.getId())).build();
		Response creationResponse = Response.created(cardUri).build();
		
		return creationResponse;
	}
	
	/**
	 * Updates the given card.
	 * 
	 * @param cardId
	 *            id of the card to update
	 * @param cardJaxb
	 *            card data
	 */
	@PUT
	@Path("{cardId}/")
	public void updateCard(@PathParam("cardId") long cardId,
			JAXBElement<CardEntry> cardJaxb) {

	}
	
	/**
	 * Deletes the given card.
	 * 
	 * @param cardId
	 *            id of the card to delete
	 */
	@DELETE
	@Path("{notebookId}/")
	public void deleteCard(@PathParam("cardId") long cardId) {
		Card foundCard = notesServiceImpl.getCard(cardId);
		if (foundCard != null) {
			notesServiceImpl.deleteCard(foundCard);
		} else {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}	 
	}
}
