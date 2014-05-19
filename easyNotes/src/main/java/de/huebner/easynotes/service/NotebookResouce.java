package de.huebner.easynotes.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;

/**
 * REST Service for Notebooks
 */
@Path("notebooks")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
// @Produces({ MediaType.APPLICATION_JSON })
// @Consumes({ MediaType.APPLICATION_JSON })
@Stateless
public class NotebookResouce {

	@EJB
	private NotesServiceImpl notesServiceImpl;

	@Context
	UriInfo uriInfo;

	/**
	 * Returns a list of all notebooks.<br>
	 * Sample request:
	 * http://127.0.0.1:8080/easyNotes-1.0.0-SNAPSHOT/rs/notebooks
	 */
	@GET
	public List<NotebookTO> getAllNotebooks() {
		List<Notebook> notebooks = notesServiceImpl.getAllNotebooks();

		NotebookMapper mapper = new NotebookMapper();
		List<NotebookTO> notebookTOs = new ArrayList<NotebookTO>(
				notebooks.size());
		Iterator<Notebook> iterator = notebooks.iterator();
		while (iterator.hasNext()) {
			NotebookTO notebookTO = mapper
					.mapEntityToTO(iterator.next(), false);
			notebookTOs.add(notebookTO);
		}

		return notebookTOs;
	}

	/**
	 * Returns the notebook with the given id. Information about the notebook
	 * and the associated categories is being returned. <br>
	 * Sample request:
	 * http://127.0.0.1:8080/easyNotes-1.0.0-SNAPSHOT/rs/notebooks/913
	 * 
	 * 
	 * @param notebookId
	 * @return found notebook
	 */
	@GET
	@Path("{notebookId}/")
	public NotebookTO getNotebook(@PathParam("notebookId") long notebookId) {
		Notebook foundNotebook = notesServiceImpl.getNotebook(notebookId);
		if (foundNotebook != null) {
			NotebookMapper mapper = new NotebookMapper();
			NotebookTO notebookTO = mapper.mapEntityToTO(foundNotebook, true);

			return notebookTO;
		}

		return null;
	}
}
