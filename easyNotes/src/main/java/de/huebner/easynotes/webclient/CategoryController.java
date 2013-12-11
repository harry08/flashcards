package de.huebner.easynotes.webclient;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import de.huebner.easynotes.businesslogic.data.Category;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;

/**
 * Controller to manage categories. This includes showing a list of categories and
 * editing a category.
 */
@ManagedBean
@SessionScoped
public class CategoryController implements Serializable {

	private static final long serialVersionUID = 1418056181428645354L;

	@EJB
	private NotesServiceImpl notesServiceImpl;

	/**
	 * Current category to edit
	 */
	private Category category = new Category();

	private List<Category> categoryList;
	
	private String editTitle;
	
	/**
	 * Indicates that the user has changed category data on this page.  
	 */
	private boolean dataChanged = false;
	
	@ManagedProperty(value="#{notebookController}")
	private NotebookController notebookController;

	public List<Category> getCategoryList() {
		if (categoryList == null) {
			retrieveCategoryList();
		}

		return categoryList;
	}

	/**
	 * Edits the selected category.
	 * 
	 * @param category
	 *          category to edit
	 * @return Page to edit the category.
	 */
	public String editCategory(Category category) {
		System.out.println("editCategory with category called. Category: " + category);
		this.category = category;
		
		editTitle = "Edit category";

		return "editCategory.xhtml";
	}
	
  /**
   * Deletes the selected category.
   * 
   * @param category
   *          category to delete
   * @return null. This shows the current page again.
   */
  public String deleteCategory(Category category) {
    notesServiceImpl.deleteCategory(category);
    dataChanged = true;

    retrieveCategoryList();
    
    return null;
  }

	/**
	 * Persists the edited category and refreshes the cardList. Returns success.
	 * Success is interpreted to show the categorylist page with the updated
	 * category list.
	 */
	public String updateCategory() {
		category = notesServiceImpl.updateCategory(category);
		dataChanged = true;
		
		retrieveCategoryList();
		
		return "success";
	}
	
	public String cancelEdit() {
		return "cancel";
	}

	/**
	 * Creates a new category instance and returns the edit page to edit this
	 * category.
	 * 
	 * @return Page to edit the new category.
	 */
	public String addNewCategory() {
		category = new Category();
		category.setTitle("new Category");
		
		editTitle = "Create a new category";

		return "editCategory.xhtml";
	}
	
	public String backToNotebookList() {
		boolean needRefresh = dataChanged;
		dataChanged = false;
		
		return notebookController.showNotebookList(needRefresh);
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public NotesServiceImpl getNotesServiceImpl() {
		return notesServiceImpl;
	}

	public void setNotesServiceImpl(NotesServiceImpl notesServiceImpl) {
		this.notesServiceImpl = notesServiceImpl;
	}
	
	public NotebookController getNotebookController() {
		return notebookController;
	}

	public void setNotebookController(NotebookController notebookController) {
		this.notebookController = notebookController;
	}

	public String getEditTitle() {
		return editTitle;
	}
	
	/**
	 * Fetches the categories from the server.
	 */
	private void retrieveCategoryList() {
		categoryList = notesServiceImpl.getAllCategories();
	}
}
