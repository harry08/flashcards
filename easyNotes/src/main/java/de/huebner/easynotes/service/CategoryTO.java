package de.huebner.easynotes.service;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CategoryTO {

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
