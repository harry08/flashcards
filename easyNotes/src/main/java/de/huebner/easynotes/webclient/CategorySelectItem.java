package de.huebner.easynotes.webclient;

import de.huebner.easynotes.businesslogic.data.Category;

/**
 * Selectable category item
 */
public class CategorySelectItem extends SelectableItem {
	
	public static final long ITEM_CAT_ALL = -1;
	public static final long ITEM_CAT_NOTSELECTED = -2;

	private Category category;
	
	public CategorySelectItem(Category category) {
		super(category.getId(), category.getTitle());
		this.category = category;
	}
	
	public CategorySelectItem(long itemId, String title) {
		super(itemId, title);
		this.category = null;
	}
	
	public Category getCategory() {
		return category;
	}

	public boolean itemIsCategory() {
		return (category != null);
	}
}
