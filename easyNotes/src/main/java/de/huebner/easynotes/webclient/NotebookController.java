package de.huebner.easynotes.webclient;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

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
	 * Represents the selected category. Used to filter the list of notebooks
	 */
	private String selectedCategoryId = String.valueOf(CategorySelectItem.ITEM_CAT_ALL);

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
	 * List with category entries to select in the dropdown box
	 */
	private List<CategorySelectItem> categorySelectItems;

	/**
	 * List with category - notebook associations. For every existing category an
	 * entry exists in this list. Each entry contains an information if this
	 * category is associated to the selected notebook.
	 */
	private List<CategoryEntry> notebookCategoryList;

	private String editTitle;
	
	private String created;
	
	private String lastEdited;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public List<Notebook> getNotebookList() {
		if (notebookList == null) {
			retrieveNotebookList();
		}

		return notebookList;
	}

	public List<Category> getCategoryList() {
		if (categoryList == null) {
			retrieveCategoryList();
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
			generateNotebookCategoryList();			
		}

		return notebookCategoryList;
	}

	/**
	 * Returns a list with CategorySelectItems to display in the category dropdown
	 * element.
	 * 
	 * @return list with CategorySelectItems
	 */
	public List<CategorySelectItem> getCategorySelectItems() {
		if (categorySelectItems == null) {
			generateCategorySelectItems();
		}

		return categorySelectItems;
	}

	private boolean notebookHasCategory(Category categoryToCheck) {
		return notebook.containsCategory(categoryToCheck);
	}
	
	/**
	 * Change method for category dropdown. The notebooklist is updated.
	 * 
	 * @param e
	 *            event
	 */
	public void categoryChanged(ValueChangeEvent e) {
		selectedCategoryId = e.getNewValue().toString();
		
		retrieveNotebookList();
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
		this.notebook = notebook;
		
		generateNotebookCategoryList();

		editTitle = "Edit notebook";
		created = sdf.format(notebook.getCreated());
		lastEdited = sdf.format(notebook.getModified());

		return "editNotebook.xhtml";
	}

	/**
	 * Persists the edited notebook and refreshes the notebookList. Returns success.
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
		retrieveNotebookList();

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
		
		generateInitialNotebookCategoryList();

		return "editNotebook.xhtml";
	}
	
	/**
	 * Deletes the given notebook
	 * 
	 * @param notebook
	 *            notebook to delete
	 * @return null. This results in the presentation of the same page
	 */
	public String deleteNotebook(Notebook notebook) {
		notesServiceImpl.deleteNotebook(notebook);

		retrieveNotebookList();

		return null;
	}
	
	/**
	 * Deletes the selected notebook
	 * 
	 * @return Page to show the updated notebooklist.
	 */
	public String deleteNotebook() {
		notesServiceImpl.deleteNotebook(notebook);

		retrieveNotebookList();

		return "listNotebooks.xhtml";
	}
	
	/**
	 * Shows the notebooklist page
	 * 
	 * @param refresh
	 *            indicates that the underlying data has to be refetched.
	 * @return notebooklist page
	 */
	public String showNotebookList(boolean refresh) {
		if (refresh) {
			retrieveCategoryList();
			generateCategorySelectItems();
			selectedCategoryId = String.valueOf(CategorySelectItem.ITEM_CAT_ALL);
			
			retrieveNotebookList();
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
		return selectedCategoryId;
	}

	public void setCategory(String category) {
		this.selectedCategoryId = category;
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
	
	public String getCreated() {
		return created;
	}
	
	public String getLastEdited() {
		return lastEdited;
	}
	
	private Category getCategoryWithId(String categoryId) {
		long catId = Long.valueOf(categoryId);
		for (Category currentCategory : categoryList) {
			if (currentCategory.getId() == catId) {
				return currentCategory;
			}
		}
		
		return null;
	}
	
	/**
	 * Generates the list with notebook - category associations.
	 */
	private void generateNotebookCategoryList() {
		// Make sure categories are init.
		getCategoryList();

		notebookCategoryList = new ArrayList<CategoryEntry>(categoryList.size());
		for (Category currentCategory : categoryList) {
			boolean entryChecked = notebookHasCategory(currentCategory);

			CategoryEntry currentEntry = new CategoryEntry(currentCategory,	entryChecked);
			notebookCategoryList.add(currentEntry);
		}
	}
	
	
	/**
	 * Generates an initial list with notebook - category associations.
	 */
	private void generateInitialNotebookCategoryList() {
	  // Make sure categories are init.
    getCategoryList();

    notebookCategoryList = new ArrayList<CategoryEntry>(categoryList.size());
    for (Category currentCategory : categoryList) {
      CategoryEntry currentEntry = new CategoryEntry(currentCategory, false);
      notebookCategoryList.add(currentEntry);
    }
	}
		
	/**
	 * Creates the list for the category dropdown
	 */
	private void generateCategorySelectItems() {
		// Make sure categories are init.
		getCategoryList();

		categorySelectItems = new ArrayList<CategorySelectItem>(categoryList.size() + 2);
		categorySelectItems.add(new CategorySelectItem(CategorySelectItem.ITEM_CAT_ALL, "All categories"));
		categorySelectItems.add(new CategorySelectItem(CategorySelectItem.ITEM_CAT_NOTSELECTED, "Not selected"));
		for (Category currentCategory : categoryList) {
			CategorySelectItem currentItem = new CategorySelectItem(currentCategory);
			categorySelectItems.add(currentItem);
		}
	}
	
	/**
	 * Fetches the categories from the server.
	 */
	private void retrieveCategoryList() {
		categoryList = notesServiceImpl.getAllCategories();
	}
	
	/**
	 * Fetches the notebooks from the server.
	 */
	private void retrieveNotebookList() {
		if (selectedCategoryId.equals(String.valueOf(CategorySelectItem.ITEM_CAT_ALL))) {
			// select all notebooks
			notebookList = notesServiceImpl.getAllNotebooks();
		} else if (selectedCategoryId.equals(String.valueOf(CategorySelectItem.ITEM_CAT_NOTSELECTED))) {
			// select notebooks with no associated category
			notebookList = notesServiceImpl.getAllNotebooksWithNoCategory();
		} else {
			// select notebooks with specified category
			Category selectedCategory = getCategoryWithId(selectedCategoryId);
			notebookList = notesServiceImpl.getAllNotebooks(selectedCategory);
		}
	}
}
