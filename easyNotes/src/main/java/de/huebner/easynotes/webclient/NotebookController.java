package de.huebner.easynotes.webclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.huebner.easynotes.businesslogic.data.Category;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;

/**
 * Controller to manage notebooks. This includes showing a list of notebooks and
 * editing a notebook.
 */
@ManagedBean
@SessionScoped
public class NotebookController implements Serializable {

	private static final long serialVersionUID = 1418056181428645354L;

	@EJB
	private NotesServiceImpl notesServiceImpl;

	/**
	 * Represents the selected category to filter the list of notebooks
	 */
	private String selectedCategory = "";

	/**
	 * Current notebook to edit
	 */
	private Notebook notebook = new Notebook();

	private List<Notebook> notebookList;

	/**
	 * List with general categories
	 */
	private List<Category> categoryList;

	/**
	 * List with category - notebook asscociations. For every existing category an
	 * entry exists in this list. Each entry contains an information if this
	 * category is associated to the selected notebook.
	 */
	private List<CategoryEntry> notebookCategoryList;

	private String editTitle;

	public List<Notebook> getNotebookList() {
		if (notebookList == null) {
			notebookList = notesServiceImpl.getAllNotebooks();
		}

		return notebookList;
	}

	public List<Category> getCategoryList() {
		if (categoryList == null) {
			categoryList = notesServiceImpl.getAllCategories();
		}

		return categoryList;
	}

	/**
	 * Returns a list with category - notebook asscociations.
	 * 
	 * @return category - notebook associations list
	 */
	public List<CategoryEntry> getNotebookCategoryList() {
		if (notebookCategoryList == null) {
			// Make sure categories are init.
			getCategoryList();

			notebookCategoryList = new ArrayList<CategoryEntry>(categoryList.size());
			for (Category currentCategory : categoryList) {
				boolean entryChecked = notebookHasCategory(currentCategory);

				CategoryEntry currentEntry = new CategoryEntry(currentCategory, entryChecked);
				notebookCategoryList.add(currentEntry);
			}
		}

		return notebookCategoryList;
	}

	private boolean notebookHasCategory(Category categoryToCheck) {
		return notebook.containsCategory(categoryToCheck);
	}

	/**
	 * Opens the category list page
	 * 
	 * @return category list page
	 */
	public String manageCategories() {
		return "listCategories.xhtml";
	}

	/**
	 * Edits the selected notebook.
	 * 
	 * @param notebook
	 *          notebook to edit
	 * @return Page to edit the notebook.
	 */
	public String editNotebook(Notebook notebook) {
		notebookCategoryList = null;

		this.notebook = notebook;

		editTitle = "Edit notebook";

		return "editNotebook.xhtml";
	}

	/**
	 * Persists the edited notebook and refreshes the cardList. Returns success.
	 * Success is interpreted to show the notebooklist page with the updated
	 * notebook list.
	 */
	public String updateNotebook() {
		// Before saving update the notebooks category list
		if (notebookCategoryList != null) {
			for (CategoryEntry entry : notebookCategoryList) {
				if (entry.isNotebookCategory()) {
					// Add category if not already included in category list
					notebook.addCategoryToNotebook(entry.getCategory());

				} else {
					// Remove category if included in category list
					notebook.removeCategoryFromNotebook(entry.getCategory());
				}
			}
		}

		notebook = notesServiceImpl.updateNotebook(notebook);
		notebookList = notesServiceImpl.getAllNotebooks();

		return "success";
	}

	public String cancelEdit() {
		return "cancel";
	}

	/**
	 * Creates a new notebook instance and returns the edit page to edit this
	 * notebook.
	 * 
	 * @return Page to edit the new notebook.
	 */
	public String addNewNotebook() {
		notebook = new Notebook();
		notebook.setTitle("new Notebook");

		editTitle = "Create a new notebook";

		return "editNotebook.xhtml";
	}
	
	public String showNotebookList(boolean refresh) {
	  if (refresh) {
      getNotebookCategoryList();
      // TODO select a category
      getNotebookList(); 
    }
	  
	  return "listNotebooks.xhtml";
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
	}

	public String getCategory() {
		return selectedCategory;
	}

	public void setCategory(String category) {
		this.selectedCategory = category;
	}

	public NotesServiceImpl getNotesServiceImpl() {
		return notesServiceImpl;
	}

	public void setNotesServiceImpl(NotesServiceImpl notesServiceImpl) {
		this.notesServiceImpl = notesServiceImpl;
	}

	public String getEditTitle() {
		return editTitle;
	}
}
