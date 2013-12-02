package de.huebner.easynotes.webclient;

import de.huebner.easynotes.businesslogic.data.Category;

/**
 * Entry for a category associated to a notebook.
 */
public class CategoryEntry {

	/**
	 * true, if this category is associated to the notebook
	 */
	private boolean notebookCategory;
	
	private String title;
	
	private Category category;
	
	public CategoryEntry(Category category, boolean notebookCategory) {
		this.category = category;
		this.title = category.getTitle();
		this.notebookCategory = notebookCategory;
	}

	public boolean isNotebookCategory() {
		return notebookCategory;
	}
	
	public boolean getNotebookCategory() {
		return notebookCategory;
	}

	public void setNotebookCategory(boolean notebookCategory) {
		this.notebookCategory = notebookCategory;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Category getCategory() {
		return category;
	}
}
