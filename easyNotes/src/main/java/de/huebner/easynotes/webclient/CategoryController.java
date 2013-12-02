package de.huebner.easynotes.webclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
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
	
	private String parentPage = "listNotebooks.xhtml";

	/**
	 * Is called from JSF after creation.
	 */
	@PostConstruct
	public void init() {
		if (notesServiceImpl != null) {
			categoryList = notesServiceImpl.getAllCategories();
		} else {
			categoryList = new ArrayList<Category>();
		}
	}

	public List<Category> getCategoryList() {
		if (categoryList == null) {
			categoryList = notesServiceImpl.getAllCategories();
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
	 * Persists the edited category and refreshes the cardList. Returns success.
	 * Success is interpreted to show the categorylist page with the updated
	 * category list.
	 */
	public String updateCategory() {
		category = notesServiceImpl.updateCategory(category);
		categoryList = notesServiceImpl.getAllCategories();

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
		return parentPage;
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

	public String getEditTitle() {
		return editTitle;
	}
}
