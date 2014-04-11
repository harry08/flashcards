package de.huebner.easynotes.webclient;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.huebner.easynotes.businesslogic.NotesServiceBusinessException;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;

/**
 * Controller to import cards.
 */
@ManagedBean
@SessionScoped
public class CardImportController implements Serializable {

	private static final long serialVersionUID = 1663822726562999006L;

	private Notebook selectedNotebook;

	private String importString;

	@ManagedProperty(value = "#{cardController}")
	private CardController cardController;

	@EJB
	private NotesServiceImpl notesServiceImpl;

	private boolean needCardRefresh = false;

	/**
	 * Imports the cards from the importString
	 * 
	 * @return
	 */
	public String importCards() {
		String message = "";
		String detail1 = "";
		String detail2 = "";
		
		try {
			int count = notesServiceImpl.importCards(importString,
					selectedNotebook);
			if (count > 1) {
				message = "Successfully imported " + count + " cards";
			} else if (count == 1) {
				message = "Successfully imported one card";
			} else {
				message = "No cards imported";
			}

			// After import tell the cardController to update the cardlist.
			needCardRefresh = true;
		} catch (NotesServiceBusinessException e) {
			e.printStackTrace();

			if (e.getErrorCode() == NotesServiceBusinessException.ERROR_IMPORT_WRONG_HEADER) {
				message = "Could not import data due to a wrong header";
			} else if (e.getErrorCode() == NotesServiceBusinessException.ERROR_IMPORT_UNEXPECTED_FIELD) {
				message = "Could not import data due to an unexpected field";
			} else if (e.getErrorCode() == NotesServiceBusinessException.ERROR_IMPORT_WRONG_FORMAT) {
				message = "Could not import data due to a wrong input format";
			} else if (e.getErrorCode() == NotesServiceBusinessException.ERROR_IMPORT_MORE_FIELDS_THAN_EXPECTED) {
				message = "Could not import data due to a record that contains more fields than expected";
			} else {
				message = "An error occurred while importing cards. No cards where imported. See error log for more details";
			}
			
			detail1 = e.getErrorValue1();
			detail2 = e.getErrorValue2();
		}

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
		
		if (detail1 != null && detail1.length() > 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
		}
		if (detail2 != null && detail2.length() > 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
		}

		return "cardImport.xhmtl";
	}

	public Notebook getSelectedNotebook() {
		return selectedNotebook;
	}

	/**
	 * Opens the import page for the selected notebook.
	 * 
	 * @return import page
	 */
	public String openImportPage(Notebook notebook) {
		this.selectedNotebook = notebook;
		importString = "";

		return "cardImport.xhmtl";
	}

	/**
	 * Navigates back to the card list.
	 * 
	 * @return cardlist page
	 */
	public String backToCardList() {
		return cardController.showCardList(needCardRefresh);
	}

	public String getImportString() {
		return importString;
	}

	public void setImportString(String importString) {
		this.importString = importString;
	}

	public CardController getCardController() {
		return cardController;
	}

	public void setCardController(CardController cardController) {
		this.cardController = cardController;
	}
}
