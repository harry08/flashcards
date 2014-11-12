package de.huebner.easynotes.client;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.huebner.easynotes.common.data.CardEntry;
import de.huebner.easynotes.common.data.NotebookEntry;

/**
 * Simple client that reads data via the easynotes Client API.
 * The application needs to be deployed on a local server. 
 */
public class EasynotesTestClient {
	
	private Easynotes easynotes = new Easynotes();
	
	private void printNotebooks(List<NotebookEntry> notebooks) {
		System.out.println(notebooks.size() + " Notebooks found");
		for (NotebookEntry notebookEntry : notebooks) {
			System.out.println(notebookEntry.getId() + " " + notebookEntry.getTitle());
		}
	}
	
	private void readData() throws EasynotesException {
		List<NotebookEntry> notebooks = easynotes.getNotebooks();
		printNotebooks(notebooks);
		
		// get detail of first notebook
		NotebookEntry notebook = easynotes.getNotebook(notebooks.get(0).getId());
		System.out.println("Details of notebook " + notebook.getId() + " "
				+ notebook.getTitle() + " read.");
		
		// get all cards of first notebook
		List<CardEntry> cards = easynotes.getCards(notebooks.get(0).getId());
		System.out.println(cards.size() + " Cards found in notebook "
				+ notebooks.get(0).getTitle());

		for (CardEntry cardEntry : cards) {
			String textSnippet = "";
			if (cardEntry.getText() != null && cardEntry.getText().length() > 0) {
				textSnippet = cardEntry.getText().substring(0, 20);
			}
			System.out.println(cardEntry.getId() + " "
					+ cardEntry.getFrontText() + " " + cardEntry.getBackText()
					+ " " + textSnippet);
		}
	}
	
	private List<NotebookEntry> readNotebooks() throws EasynotesException {
		List<NotebookEntry> notebooks = easynotes.getNotebooks();
		return notebooks;
	}
	
	private void createNotebook() throws EasynotesException {
		NotebookEntry notebook = new NotebookEntry();
		notebook.setTitle("Test notebook");

		easynotes.saveNotebook(notebook);
		System.out.println("Notebook successfully created");
	}
	
	public static void main(String[] args) throws EasynotesException {
		System.out.println("Init client...");
		EasynotesTestClient testClient = new EasynotesTestClient();
		System.out.println("Read data...");
		testClient.readData();
		System.out.println("Create new notebook...");
		testClient.createNotebook();
		System.out.println("Read data after creating...");
		List<NotebookEntry> notebooks = testClient.readNotebooks();
		testClient.printNotebooks(notebooks);
		
		System.out.println("Finished");
	}
}
