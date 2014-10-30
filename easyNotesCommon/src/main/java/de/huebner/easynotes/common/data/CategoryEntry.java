package de.huebner.easynotes.common.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CategoryEntry {

	private long id;
	
	private String title;

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
}
