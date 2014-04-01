package de.huebner.easynotes.businesslogic.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents a learncard. <br>
 * Date: 22.03.13
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "Card.findAllCards", query = "SELECT c FROM Card c"),
		@NamedQuery(name = "Card.findCardsOfNotebook", query = "SELECT c FROM Card c "
				+ "WHERE c.notebook = :notebook"),
		@NamedQuery(name = "Card.findCardWithId", query = "SELECT c FROM Card c "
				+ "WHERE c.id = :id"),
		@NamedQuery(name = "Card.findAnsweredCardsOfNotebook", query = "SELECT c FROM Card c "
				+ "WHERE c.notebook = :notebook AND c.answer = :answer"),
		@NamedQuery(name = "Card.findCardsOfNotebookRecentStudied", query = "SELECT c FROM Card c "
				+ "WHERE c.notebook = :notebook AND c.lastLearned >= :lastLearned"),
		@NamedQuery(name = "Card.findAnsweredCardsOfNotebookRecentAdded", query = "SELECT c FROM Card c "
				+ "WHERE c.notebook = :notebook AND c.created >= :created"),
		@NamedQuery(name = "Card.findAnsweredCardsOfNotebookRecentModified", query = "SELECT c FROM Card c "
				+ "WHERE c.notebook = :notebook AND c.modified >= :modified"),		
		@NamedQuery(name = "Card.findCardsForLesson", query = "SELECT c FROM Card c "
				+ "WHERE c.notebook = :notebook AND (c.nextScheduled <= :nextScheduled) "
				+ "ORDER BY c.nextScheduled") })
public class Card {

	public final static int NO_ANSWER = 0;

	public final static int ANSWER_CORRECT = 1;

	public final static int ANSWER_WRONG = 2;

	public static final int TITLE_GEN_AUTOMATICALLY_TRUE = 1;

	public static final int TITLE_GEN_AUTOMATICALLY_FALSE = 0;

	/**
	 * Internal unique id of the note
	 */
	@Id
	@GeneratedValue
	private long id;

	/**
	 * Notebook of the card. One notebook has many cards.
	 */
	@ManyToOne
	@JoinColumn(name = "NOTEBOOK_ID", referencedColumnName = "ID")
	private Notebook notebook;

	/**
	 * Creation date of the note
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	/**
	 * Date of last modification of the note
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;

	/**
	 * Type of the notebook. e.g., note. The constansts for the different types
	 * are defined in CommonConstants
	 */
	private int noteType;

	/**
	 * Fronttext of the card
	 */
	private String frontText;

	/**
	 * Backtext of the card
	 */
	private String backText;

	/**
	 * Descriptiontext of the card
	 */
	@Column(length=3000)
	private String text;

	/**
	 * Date when this card is last learned
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLearned;

	/**
	 * Date when the card should be learned next
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date nextScheduled;

	/**
	 * current answer
	 */
	private int answer;

	/**
	 * nr of times this card is answered consecutively correct
	 */
	private int nrOfCorrect;

	/**
	 * nr of times this card is answered correct
	 */
	private int nrOfCorrectTotal;

	/**
	 * nr of times this card is answered consecutively wrong
	 */
	private int nrOfWrong;

	/**
	 * nr of times this card is answered wrong
	 */
	private int nrOfWrongTotal;

	/**
	 * Current compartment of the card. This value is only filled, if the card is
	 * scheduled.
	 */
	private int compartment;
	
	/**
	 * Indicator that the card has been studied. This value is not saved in the database.
	 */
	private transient boolean studied;

	/**
	 * Default constructor
	 */
	public Card() {
		super();
		setNoteType(DataConstants.TYPE_FLASHCARD);
	}

	public long getId() {
		return id;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
	}

	public String getText() {
		return text;
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

	public void setText(String text) {
		this.text = text;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public int getNoteType() {
		return noteType;
	}

	public void setNoteType(int noteType) {
		this.noteType = noteType;
	}

	/**
	 * Returns the fronttext of the card. The title of the underlying note is used
	 * as the fronttext
	 * 
	 * @return fronttext
	 */
	public String getFrontText() {
		return frontText;
	}

	/**
	 * Sets the fronttext of the card.
	 * 
	 * @param frontText
	 *          text to set
	 */
	public void setFrontText(String frontText) {
		this.frontText = frontText;
	}

	public String getBackText() {
		return backText;
	}

	public void setBackText(String backText) {
		this.backText = backText;
	}

	public Date getLastLearned() {
		return lastLearned;
	}

	public void setLastLearned(Date lastLearned) {
		this.lastLearned = lastLearned;
	}

	public Date getNextScheduled() {
		return nextScheduled;
	}

	public void setNextScheduled(Date nextScheduled) {
		this.nextScheduled = nextScheduled;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public int getNrOfCorrect() {
		return nrOfCorrect;
	}

	public void setNrOfCorrect(int nrOfCorrect) {
		this.nrOfCorrect = nrOfCorrect;
	}

	public int getNrOfWrong() {
		return nrOfWrong;
	}

	public void setNrOfWrong(int nrOfWrong) {
		this.nrOfWrong = nrOfWrong;
	}

	public int getCompartment() {
		return compartment;
	}

	public void setCompartment(int compartment) {
		this.compartment = compartment;
	}

	public int getNrOfCorrectTotal() {
		return nrOfCorrectTotal;
	}

	public void setNrOfCorrectTotal(int nrOfCorrectTotal) {
		this.nrOfCorrectTotal = nrOfCorrectTotal;
	}

	public int getNrOfWrongTotal() {
		return nrOfWrongTotal;
	}

	public void setNrOfWrongTotal(int nrOfWrongTotal) {
		this.nrOfWrongTotal = nrOfWrongTotal;
	}

	public boolean isAnswered() {
		return (getAnswer() == ANSWER_CORRECT || getAnswer() == ANSWER_WRONG);
	}

	public boolean isAnsweredCorrect() {
		return getAnswer() == ANSWER_CORRECT;
	}

	public boolean isAnsweredWrong() {
		return getAnswer() == ANSWER_WRONG;
	}
	
	public boolean isStudied() {
		//return isAnswered() || studied == true;
		return studied == true;
	}

	public void setAnsweredCorrect() {
		setAnswer(ANSWER_CORRECT);
		incNrOfCorrect();
		nrOfWrong = 0;
		
		studied = true;
	}
	
	public void markAsLearned(int maxAnswers) {
		setAnswer(ANSWER_CORRECT);
		
		nrOfCorrect = maxAnswers;
		nrOfWrong = 0;
		
		studied = true;
	}

	public void setAnsweredWrong() {
		setAnswer(ANSWER_WRONG);
		incNrOfWrong();
		nrOfCorrect = 0;
		
		studied = true;
	}
	
	public void resetProgress() {
		setAnswer(NO_ANSWER);
		
		nrOfCorrect = 0;
		nrOfWrong = 0;	
		
		studied = true;
	}

	public void resetCurrentStudyInfo() {
		setAnswer(NO_ANSWER);
		studied = false;
	}

	private void incNrOfCorrect() {
		int cardsCorrect = nrOfCorrect;
		cardsCorrect++;
		nrOfCorrect = cardsCorrect;

		int cardsCorrectTotal = nrOfCorrectTotal;
		cardsCorrectTotal++;
		nrOfCorrectTotal = cardsCorrectTotal;
	}

	private void incNrOfWrong() {
		int cardsWrong = nrOfWrong;
		cardsWrong++;
		nrOfWrong = cardsWrong;

		int cardsWrongTotal = nrOfWrongTotal;
		cardsWrongTotal++;
		nrOfWrongTotal = cardsWrongTotal;
	}
	
	@Override
	public boolean equals(Object o) {
		return ((o instanceof Card) && id == ((Card) o).getId()
				&& frontText.equals(((Card) o).getFrontText()) && backText
					.equals(((Card) o).getBackText()));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Card");
		sb.append("{id=").append(id);
		sb.append(", fronttext='").append(frontText).append('\'');
		sb.append(", backtext='").append(backText).append('\'');
		sb.append(", text='").append(text).append('\'');		
		if (modified != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			sb.append(", modified=").append(sdf.format(modified)).append('\'');
		}
		if (notebook != null) {
			sb.append(", notebook=").append(notebook.getTitle());
		}
		sb.append('}');
		return sb.toString();
	}
}
