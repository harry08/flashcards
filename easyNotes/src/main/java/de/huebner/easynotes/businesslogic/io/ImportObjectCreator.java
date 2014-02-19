package de.huebner.easynotes.businesslogic.io;

import java.util.List;

import de.huebner.easynotes.businesslogic.data.Card;

/**
 * Creator for objects during import. Used by CsvImporter
 */
public interface ImportObjectCreator {

	/**
	 * Creates a new object
	 * 
	 * @return newly created object
	 */
	public Card createNewObject();

	/**
	 * Sets the list of fields of the csv string.
	 * 
	 * @param headerFields
	 *            list of fields.
	 * @throws CardsImporterException
	 *             Thrown when there is an error in the field list
	 */
	public void setFieldNames(List<String> headerFields)
			throws CardsImporterException;

	/**
	 * Updates the given field with a value.
	 * 
	 * @param field
	 *            field to update
	 * @param value
	 *            value to set
	 * @throws CardsImporterException
	 *             Thrown when the field could not be updated. This could be due
	 *             to a wrong field name or a wrong input format.
	 */
	public void updateValue(String field, String value)
			throws CardsImporterException;
}
