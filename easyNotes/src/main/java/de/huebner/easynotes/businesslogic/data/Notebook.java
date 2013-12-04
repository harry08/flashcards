package de.huebner.easynotes.businesslogic.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents a container for notes and cards. <br>
 * Date: 16.11.12
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Notebook.findAllNotebooks", query = "SELECT nb FROM Notebook nb"),
    @NamedQuery(name = "Notebook.findNotebooksForCategory", query = "SELECT nb FROM Notebook nb JOIN nb.categories cat "
        + " WHERE cat.id = :catid"),
    @NamedQuery(name = "Notebook.findNotebookWithId", query = "SELECT nb FROM Notebook nb WHERE nb.id = :id") })
public class Notebook {

  /**
   * Internal unique id of the notebook
   */
  @Id
  @GeneratedValue
  private long id;

  /**
   * Title of the notebook
   */
  private String title;

  /**
   * Creation date of the notebook
   */
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  /**
   * Date of last modification of the notebook
   */
  @Temporal(TemporalType.TIMESTAMP)
  private Date modified;

  /**
   * Type of the notebook. e.g., notes. A notebook with type note contains only
   * notes.
   */
  private int notebookType;

  /**
   * A notebook could be associated with several categories.
   */
  @ManyToMany
  @JoinTable(name = "NB_CATEGORY", joinColumns = @JoinColumn(name = "NOTEBOOK_ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID"))
  private Collection<Category> categories;

  /**
   * Standard constructor.
   */
  public Notebook() {
    // empty
  }

  public Notebook(String title, Date created, Date modified, int notebookType) {
    this.title = title;
    this.created = created;
    this.modified = modified;
    this.notebookType = notebookType;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public Date getCreated() {
    return created;
  }

  public Date getModified() {
    return modified;
  }

  protected void setId(long id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public int getNotebookType() {
    return notebookType;
  }

  public void setNotebookType(int notebookType) {
    this.notebookType = notebookType;
  }

  public Collection<Category> getCategories() {
    if (categories == null) {
      categories = new ArrayList<Category>();
    }
    return categories;
  }

  public boolean containsCategory(Category category) {
    getCategories();
    if (categories != null) {
      for (Category currentCategory : categories) {
        if (currentCategory.equals(category)) {
          return true;
        }
      }
    }

    return false;
  }

  public void addCategoryToNotebook(Category category) {
    if (!containsCategory(category)) {
      categories.add(category);
    }
  }

  public void removeCategoryFromNotebook(Category category) {
    if (containsCategory(category)) {
      categories.remove(category);
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Notebook");
    sb.append("{id=").append(id);
    sb.append(", title='").append(title).append('\'');
    sb.append(", type=").append(notebookType);
    sb.append('}');
    return sb.toString();
  }
}
