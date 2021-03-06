package de.huebner.easynotes.webclient;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;
import de.huebner.easynotes.businesslogic.io.CardExportData;
import de.huebner.easynotes.common.CommonConstants;

/**
 * Controller to export cards.
 */
@ManagedBean
@SessionScoped
public class CardExportController implements Serializable {

	private static final long serialVersionUID = -7941017183633596302L;

	private Notebook selectedNotebook;

	private String exportString = "";

	/**
	 * Represents the mode, how the data is being exported. i.e. which fields
	 * will be in the output. Is filled over the selectOneMenu.
	 */
	private String exportMode = "";

	/**
	 * Represents the format used to export the cards. Either csv or custom with
	 * selfdefinied delimiter.
	 */
	private String exportFormat = "";
	
	/**
	 * Flag to control if a header line should be included in the export string.
	 */
	private boolean containsHeader;
	
	private String wordDelimiter;

	private String recordDelimiter;

	@ManagedProperty(value = "#{cardController}")
	private CardController cardController;

	@EJB
	private NotesServiceImpl notesServiceImpl;

	/**
	 * Exports the cards to the exportString. The exported data are displayed in
	 * the exportString field.
	 * 
	 * @return
	 */
	public String exportCards() {
		String wordDel;
		String recordDel;
		boolean quoteDelimiter;

		if (exportFormat.equals("csv")) {
			wordDel = CommonConstants.SEMICOLON;
			recordDel = CommonConstants.END_OF_LINE;
			quoteDelimiter = true;
		} else {
			wordDel = wordDelimiter;
			recordDel = recordDelimiter;
			quoteDelimiter = false;
		}

		CardExportData cardExportData = notesServiceImpl.exportCards(
				selectedNotebook, getExportModeValue(), wordDel, recordDel, quoteDelimiter, containsHeader);
		exportString = cardExportData.getExportString();

		int count = cardExportData.getNrOfCards();
		String message = "";
		if (count > 1) {
			message = "Successfully exported " + count + " cards";
		} else if (count == 1) {
			message = "Successfully exported one card";
		} else {
			message = "No cards exported";
		}
		
		FacesMessage info = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
		FacesContext.getCurrentInstance().addMessage(null, info);
	
		return "cardExport.xhmtl";
	}

	private int getExportModeValue() {
		if (exportMode.equals("2")) {
			return CommonConstants.EXPORTTYPE_FRONTBACK;
		} else if (exportMode.equals("3")) {
			return CommonConstants.EXPORTTYPE_BACKFRONT;
		} else if (exportMode.equals("4")) {
			return CommonConstants.EXPORTTYPE_FRONT_BACKDESC;
		} else if (exportMode.equals("5")) {
			return CommonConstants.EXPORTTYPE_ALL_FIELDS;
		} else {
			return CommonConstants.EXPORTTYPE_STANDARD;
		}
	}

	/**
	 * Opens the export page for the selected notebook.
	 * 
	 * @return import page
	 */
	public String openExportPage(Notebook notebook) {
		this.selectedNotebook = notebook;
		exportString = "";
		// Preset values
		exportMode = "1";  // Standard fieldset with fronttext, backtext and description
		containsHeader = true;
		exportFormat = "custom";
		wordDelimiter = "<WORD>";
		recordDelimiter = "<END>";

		return "cardExport.xhmtl";
	}

	public Notebook getSelectedNotebook() {
		return selectedNotebook;
	}

	/**
	 * Navigates back to the card list.
	 * 
	 * @return cardlist page
	 */
	public String backToCardList() {
		return cardController.showCardList(false);
	}

	public String getExportString() {
		return exportString;
	}

	public void setExportString(String exportString) {
		this.exportString = exportString;
	}

	public String getExportMode() {
		return exportMode;
	}

	public void setExportMode(String exportMode) {
		this.exportMode = exportMode;
	}

	public String getWordDelimiter() {
		return wordDelimiter;
	}

	public void setWordDelimiter(String wordDelimiter) {
		this.wordDelimiter = wordDelimiter;
	}

	public String getRecordDelimiter() {
		return recordDelimiter;
	}

	public void setRecordDelimiter(String recordDelimiter) {
		this.recordDelimiter = recordDelimiter;
	}

	public CardController getCardController() {
		return cardController;
	}

	public void setCardController(CardController cardController) {
		this.cardController = cardController;
	}

	public String getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(String exportFormat) {
		this.exportFormat = exportFormat;
	}
}
