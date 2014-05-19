package de.huebner.easynotes.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.huebner.easynotes.businesslogic.data.Category;
import de.huebner.easynotes.businesslogic.data.Notebook;

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
	public NotebookTO mapEntityToTO(Notebook notebook, boolean mapCategories) {
		NotebookTO notebookTO = new NotebookTO();
		notebookTO.setId(notebook.getId());
		notebookTO.setTitle(notebook.getTitle());

		if (mapCategories) {
			Collection<Category> categories = notebook.getCategories();
			Iterator<Category> iter = categories.iterator();
			List<CategoryTO> categoryTOList = new ArrayList<CategoryTO>();
			while (iter.hasNext()) {
				Category currentCategory = iter.next();

				CategoryTO categoryTO = new CategoryTO();
				categoryTO.setId(currentCategory.getId());
				categoryTO.setTitle(currentCategory.getTitle());
				categoryTOList.add(categoryTO);
			}

			if (categoryTOList.size() > 0) {
				notebookTO.setNotebookCategories(categoryTOList);
			}
		}

		return notebookTO;
	}
}
