package de.huebner.easynotes.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Category;
import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.common.data.CategoryEntry;
import de.huebner.easynotes.common.data.NotebookEntry;

public class NotebookMapper {

	/**
	 * Maps a given Notebook entity to a newly created Notebook transport
	 * object.
	 * 
	 * @param notebook
	 *            Notebook entity to map
	 * @param mapCategories
	 *            Flag to indicate if notebook categories should be mapped.
	 * @return Notebook transport object
	 */
	public NotebookEntry mapEntityToTO(Notebook notebook, boolean mapCategories) {
		NotebookEntry notebookEntry = new NotebookEntry();
		notebookEntry.setId(notebook.getId());
		notebookEntry.setTitle(notebook.getTitle());

		if (mapCategories) {
			Collection<Category> categories = notebook.getCategories();
			Iterator<Category> iter = categories.iterator();
			List<CategoryEntry> categoryEntryList = new ArrayList<CategoryEntry>();
			while (iter.hasNext()) {
				Category currentCategory = iter.next();

				CategoryEntry categoryEntry = new CategoryEntry();
				categoryEntry.setId(currentCategory.getId());
				categoryEntry.setTitle(currentCategory.getTitle());
				categoryEntryList.add(categoryEntry);
			}

			if (categoryEntryList.size() > 0) {
				notebookEntry.setNotebookCategories(categoryEntryList);
			}
		}

		return notebookEntry;
	}

	public void mapTOToEntity(NotebookEntry notebookEntry, Notebook notebook) {
		notebook.setTitle(notebookEntry.getTitle());
	}
}
