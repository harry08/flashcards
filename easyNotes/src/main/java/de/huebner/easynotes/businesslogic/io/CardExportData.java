package de.huebner.easynotes.businesslogic.io;

/**
 * Structure for an export String.
 */
public class CardExportData {

	private int nrOfCards;
	
	private String exportString;
	
	public CardExportData(int nrOfCards, String exportString) {
		this.nrOfCards = nrOfCards;
		this.exportString = exportString;
	}

	public int getNrOfCards() {
		return nrOfCards;
	}

	public String getExportString() {
		return exportString;
	}
}
