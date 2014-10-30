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

import de.huebner.easynotes.common.data.CardEntry;
import de.huebner.easynotes.common.data.NotebookEntry;

/**
 * Simple client that reads data via the REST API.
 */
public class SimpleRestClient {

	private Client client;
	private WebTarget restTarget;

	private final static String BASE_URL = "http://127.0.0.1:8080/rs";

	/**
	 * Reads a list of all notebooks URL: /notebooks
	 * 
	 * @return List with notebook objects
	 */
	private List<NotebookEntry> getNotebooks() {
		List<NotebookEntry> notebooks = null;
		try {
			notebooks = restTarget.path("notebooks")
					.request(MediaType.APPLICATION_XML)
					.get(new GenericType<List<NotebookEntry>>() {
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
	 * Reads details from one notebook. URL: /notebooks/2
	 * 
	 * @param notebookId
	 * @return found notebook
	 */
	private NotebookEntry getNotebook(long notebookId) {
		NotebookEntry notebookEntry = null;

		try {
			notebookEntry = restTarget.path("notebooks").path("" + notebookId)
					.request(MediaType.APPLICATION_XML)
					.get(new GenericType<NotebookEntry>() {
					});
			if (notebookEntry == null) {
				System.out.println("No notebook found");
			}
		} catch (WebApplicationException ex) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return notebookEntry;
	}

	/**
	 * Reads all cards from a given notebook URL: /cards/query?notebookId=2
	 * 
	 * @param notebookId
	 * @return List of cards
	 */
	private List<CardEntry> getCards(long notebookId) {
		List<CardEntry> cards = null;
		try {
			cards = restTarget.path("cards/query")
					.queryParam("notebookId", notebookId)
					.request(MediaType.APPLICATION_XML)
					.get(new GenericType<List<CardEntry>>() {
					});
			if (cards == null) {
				System.out.println("No cards found");
			}
		} catch (WebApplicationException ex) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return cards;
	}

	private void createNotebook() {
		NotebookEntry notebook = new NotebookEntry();
		notebook.setTitle("Test notebook");

		Response response = restTarget
				.path("notebooks")
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(notebook, MediaType.APPLICATION_XML),
						Response.class);
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			System.out.println("Notebook successfully created");
			// System.out.println("URI of the new notebook: " +
			// response.getLocation());
		} else {
			System.out.println("Error creating notebook");
		}
	}

	private void deleteNotebook(long notebookId) {
		try {
			restTarget.path("notebooks").path("" + notebookId)
					.request(MediaType.APPLICATION_XML).delete();
			System.out.println("Notebook with id " + notebookId + " deleted");
		} catch (WebApplicationException ex) {
			System.out.println("Exception deleting the notebook: "
					+ ex.getMessage());
		}
	}
	
	private List<NotebookEntry> readNotebooks() {
		// get all notebooks
		List<NotebookEntry> notebooks = getNotebooks();
		return notebooks;
	}
	
	private void printNotebooks(List<NotebookEntry> notebooks) {
		System.out.println(notebooks.size() + " Notebooks found");
		for (NotebookEntry notebookEntry : notebooks) {
			System.out.println(notebookEntry.getId() + " " + notebookEntry.getTitle());
		}
	}

	private void readData() {
		List<NotebookEntry> notebooks = readNotebooks();
		printNotebooks(notebooks);
		
		// get detail of first notebook
		NotebookEntry notebook = getNotebook(notebooks.get(0).getId());
		System.out.println("Details of notebook " + notebook.getId() + " "
				+ notebook.getTitle() + " read.");

		// get all cards of first notebook
		List<CardEntry> cards = getCards(notebooks.get(0).getId());
		System.out.println(notebooks.size() + " Cards found in notebook "
				+ notebooks.get(0).getTitle());

		for (CardEntry cardEntry : cards) {
			String textSnippet = "";
			if (cardEntry.getText() != null && cardEntry.getText().length() > 0) {
				textSnippet = cardEntry.getText().substring(0, 20);
			}
			System.out.println(cardEntry.getId() + " " + cardEntry.getFrontText()
					+ " " + cardEntry.getBackText() + " " + textSnippet);
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
		System.out.println("Init client...");
		SimpleRestClient restClient = new SimpleRestClient();
		restClient.initClient();

		System.out.println("Read data...");
		restClient.readData();
		System.out.println("Create new notebook...");
		restClient.createNotebook();
		List<NotebookEntry> notebooks = restClient.readNotebooks();
		restClient.printNotebooks(notebooks);
		
		System.out.println("Finished");
		restClient.closeClient();
	}
}
