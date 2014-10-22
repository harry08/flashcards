package de.huebner.easynotes.service;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transport object for a card
 */
@XmlRootElement
public class CardTO {
	
	private long id;
	
	private String frontText;
	
	private String backText;
	
	private String text;
	
	private Date lastLearned;
	
	private int compartment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getFrontText() {
		return frontText;
	}

	public void setFrontText(String frontText) {
		this.frontText = frontText;
	}

	public String getBackText() {
		return backText;
	}

	public void setBackText(String backText) {
		this.backText = backText;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getLastLearned() {
		return lastLearned;
	}

	public void setLastLearned(Date lastLearned) {
		this.lastLearned = lastLearned;
	}

	public int getCompartment() {
		return compartment;
	}

	public void setCompartment(int compartment) {
		this.compartment = compartment;
	}	
}
