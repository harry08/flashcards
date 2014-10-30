package de.huebner.easynotes.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import de.huebner.easynotes.businesslogic.data.Notebook;
import de.huebner.easynotes.businesslogic.impl.NotesServiceImpl;
import de.huebner.easynotes.common.data.NotebookEntry;

/**
 * REST Service for Notebooks
 */
@Path("/notebooks")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
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
	public List<NotebookEntry> getAllNotebooks() {
		List<Notebook> notebooks = notesServiceImpl.getAllNotebooks();

		NotebookMapper mapper = new NotebookMapper();
		List<NotebookEntry> notebookEntries = new ArrayList<NotebookEntry>(
				notebooks.size());
		Iterator<Notebook> iterator = notebooks.iterator();
		while (iterator.hasNext()) {
			NotebookEntry notebookEntry = mapper
					.mapEntityToTO(iterator.next(), false);
			notebookEntries.add(notebookEntry);
		}

		return notebookEntries;
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
	public NotebookEntry getNotebook(@PathParam("notebookId") long notebookId) {
		Notebook foundNotebook = notesServiceImpl.getNotebook(notebookId);
		if (foundNotebook != null) {
			NotebookMapper mapper = new NotebookMapper();
			NotebookEntry notebookEntry = mapper.mapEntityToTO(foundNotebook, true);

			return notebookEntry;
		} else {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}		
	}
	
	/**
	 * Creates a new notebook with the given data.
	 * 
	 * @param notebookJaxb
	 *            notebook data
	 * @return Response with the URI of the newly created notebook
	 */
	@POST
	public Response createNewNotebook(JAXBElement<NotebookEntry> notebookJaxb) {
		NotebookEntry notebookEntry = notebookJaxb.getValue();
		
		NotebookMapper mapper = new NotebookMapper();
		Notebook notebook = new Notebook();
		mapper.mapTOToEntity(notebookEntry, notebook);
		notebook = notesServiceImpl.updateNotebook(notebook);

		URI notebookUri = uriInfo.getAbsolutePathBuilder()
				.path(String.valueOf(notebook.getId())).build();
		return Response.created(notebookUri).build();
	}
	
	/**
	 * Updates the given notebook.
	 * 
	 * @param notebookId
	 *            if of the notebook to update
	 * @param notebookJaxb
	 *            notebook data
	 */
	@PUT
	@Path("{notebookId}/")
	public void updateNotebook(@PathParam("notebookId") long notebookId,
			JAXBElement<NotebookEntry> notebookJaxb) {

	}

	/**
	 * Deletes the given notebook.
	 * 
	 * @param notebookId
	 *            if of the notebook to delete
	 */
	@DELETE
	@Path("{notebookId}/")
	public void deleteNotebook(@PathParam("notebookId") long notebookId) {
		Notebook foundNotebook = notesServiceImpl.getNotebook(notebookId);
		if (foundNotebook != null) {
			notesServiceImpl.deleteNotebook(foundNotebook);
		} else {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}	 
	}
}
