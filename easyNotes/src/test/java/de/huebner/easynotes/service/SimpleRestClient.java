package de.huebner.easynotes.service;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Simple client that reads data via the REST API.
 */
public class SimpleRestClient {
	
	private Client client;
	private WebTarget restTarget;
	
	private final static String BASE_URL = "http://127.0.0.1:8080/rs";

	/**
	 * Reads a list of all notebooks
	 * URL: /notebooks
	 * 
	 * @return
	 */
	private List<NotebookTO> getNotebooks() {
		List<NotebookTO> notebooks = null;
		try {
			notebooks = restTarget.path("notebooks")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<NotebookTO>>() {
            });
            if (notebooks == null) {
                System.out.println("No notebooks found");
            } 
        } catch (WebApplicationException ex) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
		
		return notebooks;
	}
	
	/**
	 * Reads details from one notebook.
	 * URL: /notebooks/2
	 * 
	 * @param notebookId
	 * @return
	 */
	private NotebookTO getNotebook(long notebookId) {
		NotebookTO notebookTO = null;
		
		try {
			notebookTO = restTarget.path("notebooks")
					.path("" + notebookId)
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<NotebookTO>() {
            });
            if (notebookTO == null) {
                System.out.println("No notebook found");
            } 
        } catch (WebApplicationException ex) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
		
		return notebookTO;
	}
	
	/**
	 * Reads all cards from a given notebook
	 * URL: /cards/query?notebookId=2
	 * 
	 * @param notebookId
	 * @return
	 */
	private List<CardTO> getCards(long notebookId) {
		List<CardTO> cards = null;
		try {
			cards = restTarget.path("cards/query")
					.queryParam("notebookId", notebookId)
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<CardTO>>() {
            });
            if (cards == null) {
                System.out.println("No cards found");
            } 
        } catch (WebApplicationException ex) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
		
		return cards;
	}
	
	private void createAndReadNotebook() {
		NotebookTO notebook = new NotebookTO();
		notebook.setTitle("Test notebook");
		
		Response response = restTarget.path("notebooks")
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(notebook, MediaType.APPLICATION_XML), Response.class);
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
	        System.out.println("Notebook successfully created"); 
	        System.out.println("URI of the new notebook: " + response.getLocation());
	    } else {
	    	System.out.println("Error creating notebook");
	    }
	}


	private void readData() {
		// get all notebooks
		List<NotebookTO> notebooks = getNotebooks();
		System.out.println(notebooks.size() + " Notebooks found");	
		
		for (NotebookTO notebookTO : notebooks) {
			System.out.println(notebookTO.getId() + " " + notebookTO.getTitle());
		}
		
		// get detail of first notebook
		NotebookTO notebook = getNotebook(notebooks.get(0).getId());
		System.out.println("Details of notebook " + notebook.getId() + " " + notebook.getTitle() + " read.");
				
		// get all cards of first notebook
		List<CardTO> cards = getCards(notebooks.get(0).getId());
		System.out.println(notebooks.size() + " Cards found in notebook " + notebooks.get(0).getTitle());	
		
		for (CardTO cardTO : cards) {
			String textSnippet = "";
			if (cardTO.getText() != null && cardTO.getText().length() > 0) {
				textSnippet = cardTO.getText().substring(0, 20);
			}
			System.out.println(cardTO.getId() + " " + cardTO.getFrontText() + " " + cardTO.getBackText()
					+ " " + textSnippet);
		}	
	}
	
	private void initClient() {
		client = ClientBuilder.newClient();
		restTarget = client.target(BASE_URL);		
	}
	
	private void closeClient() {
		client.close();
	}
	
	public static void main(String[] args) {
		SimpleRestClient restClient = new SimpleRestClient();
		restClient.initClient();
		
		restClient.readData();
		restClient.createAndReadNotebook();
		
		restClient.closeClient();
	}
}
