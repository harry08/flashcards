package de.huebner.easynotes.service;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transport object for a notebook
 */
@XmlRootElement
public class NotebookTO {
	
	private long id;
	
	private String title;
	
	private List<CategoryTO> notebookCategories;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<CategoryTO> getNotebookCategories() {
		return notebookCategories;
	}
	
	public void setNotebookCategories(List<CategoryTO> notebookCategories) {
		this.notebookCategories = notebookCategories;
	}
}
