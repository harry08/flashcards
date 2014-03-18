package de.huebner.easynotes.webclient;

/**
 * Base Class for items of a Select widgets like a selectOneMenu
 */
public class SelectableItem {

	private long itemId;
	private String title;

	public SelectableItem(long itemId) {
		this.itemId = itemId;
	}
	
	public SelectableItem(long itemId, String title) {
		this.itemId = itemId;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getItemId() {
		return itemId;
	}
}