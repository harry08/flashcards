package de.huebner.easynotes.common.data;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NotebookEntry {
	private long id;
	
	private String title;
	
	private List<CategoryEntry> notebookCategories;

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
	
	public List<CategoryEntry> getNotebookCategories() {
		return notebookCategories;
	}
	
	public void setNotebookCategories(List<CategoryEntry> notebookCategories) {
		this.notebookCategories = notebookCategories;
	}
}
