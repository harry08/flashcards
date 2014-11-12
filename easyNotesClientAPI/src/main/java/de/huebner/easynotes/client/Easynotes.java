package de.huebner.easynotes.client;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.huebner.easynotes.common.data.CardEntry;
import de.huebner.easynotes.common.data.NotebookEntry;

/**
 * Client class to access the easyNotes REST-service
 */
public class Easynotes {
	
	private String baseURL;
	
	private Client client;
	private WebTarget restTarget;
	
	public Easynotes() {
		baseURL = "http://127.0.0.1:8080/rs";	
		
		initClient();
	}
	
	private void initClient() {
		client = ClientBuilder.newClient();
		restTarget = client.target(baseURL);
	}

	public void closeClient() {
		client.close();
	}
	
	private WebTarget getTarget(String path) {
		return restTarget.path(path);
	} 
	
	/**
	 * Builds a request to the targeted webresource with the given path.
	 * 
	 * @param path
	 * @return Builder for the request
	 */
	private Invocation.Builder request(String path) {
		return getTarget(path).request(MediaType.APPLICATION_XML);
	}
	
	/**
	 * Returns a list of all notebooks
	 * 
	 * @return List with notebook objects
	 * @throws EasynotesException 
	 */
	public List<NotebookEntry> getNotebooks() throws EasynotesException {
		List<NotebookEntry> notebooks = null;
		
		try {
			notebooks = request("notebooks")
					.get(new GenericType<List<NotebookEntry>>() {
					});
		} catch (WebApplicationException ex) {
			throw new EasynotesException(ex.getResponse().getStatus());			
		}

		return notebooks;
	}
	
	/**
	 * Returns a notebook with the given id
	 * 
	 * @param notebookId
	 * @return found notebook
	 * @throws EasynotesException 
	 */
	public NotebookEntry getNotebook(long notebookId) throws EasynotesException {
		NotebookEntry notebookEntry = null;

		try {
			notebookEntry = request("notebooks/" + notebookId)
					.get(new GenericType<NotebookEntry>() {
					});
			if (notebookEntry == null) {
				// TODO
			}
		} catch (WebApplicationException ex) {
			throw new EasynotesException(ex.getResponse().getStatus());
		}

		return notebookEntry;
	}
	
	/**
	 * Returns all cards of the notebook with the given id 
	 * 
	 * @param notebookId
	 * @return List of cards
	 * @throws EasynotesException
	 */
	public List<CardEntry> getCards(long notebookId) throws EasynotesException {
		List<CardEntry> cards = null;
		try {
			cards = getTarget("cards/query")
					.queryParam("notebookId", notebookId)
					.request(MediaType.APPLICATION_XML)
					.get(new GenericType<List<CardEntry>>() {
					});			
		} catch (WebApplicationException ex) {
			throw new EasynotesException(ex.getResponse().getStatus());
		}

		return cards;
	}
	
	/**
	 * Saves the given notebook
	 * 
	 * @param notebook
	 *            notebook to save
	 * @throws EasynotesException
	 */
	public void saveNotebook(NotebookEntry notebook) throws EasynotesException {
		Response response = request("notebooks").post(
				Entity.entity(notebook, MediaType.APPLICATION_XML),
				Response.class);
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			// TODO
		} else {
			throw new EasynotesException(response.getStatus());
		}
	}
	
	/**
	 * Deletes the notebook with the given id
	 * 
	 * @param notebookId
	 *            notebook to delete
	 * @throws EasynotesException
	 */
	public void deleteNotebook(long notebookId) throws EasynotesException {
		try {
			request("notebooks/" + notebookId).delete();
			System.out.println("Notebook with id " + notebookId + " deleted");
		} catch (WebApplicationException ex) {
			System.out.println("Exception deleting the notebook: "
					+ ex.getMessage());
			throw new EasynotesException(ex.getResponse().getStatus());
		}
	}
}
