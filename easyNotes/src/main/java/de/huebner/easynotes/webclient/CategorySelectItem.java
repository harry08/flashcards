package de.huebner.easynotes.webclient;

import de.huebner.easynotes.businesslogic.data.Category;

/**
 * Selectable category item
 */
public class CategorySelectItem {
	
	public static final long ITEM_CAT_ALL = -1;
	public static final long ITEM_CAT_NOTSELECTED = -2;

	private long itemId;
	
	private String title;
	
	private Category category;
	
	public CategorySelectItem(Category category) {
		this.category = category;
		this.title = category.getTitle();
		this.itemId = category.getId();
	}
	
	public CategorySelectItem(long itemId, String title) {
		this.category = null;
		this.title = title;
		this.itemId = itemId;
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

	public long getItemId() {
		return itemId;
	}
	
	public boolean itemIsCategory() {
		return (category != null);
	}
}
