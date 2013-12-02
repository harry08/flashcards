package de.huebner.easynotes.businesslogic.data;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents a category of a notebook. <br>
 * Date: 20.11.13
 */
@Entity
@NamedQueries({@NamedQuery(name = "Category.findAllCategories", query = "SELECT c FROM Category c")})
public class Category {

  /**
   * Internal unique id of the category
   */
  @Id
  @GeneratedValue
  private long id;

  /**
   * Creation date
   */
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  /**
   * Date of last modification
   */
  @Temporal(TemporalType.TIMESTAMP)
  private Date modified;

  /**
   * title of the category
   */
  private String title;

  @ManyToMany(mappedBy = "categories")
  private Collection<Notebook> notebooks;

  /**
   * Default constructor
   */
  public Category() {
    super();
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Category");
    sb.append("{id=").append(id);
    sb.append(", title='").append(title);
    sb.append('}');
    return sb.toString();
  }
}
