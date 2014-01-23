package de.huebner.easynotes.businesslogic.study;

import java.io.Serializable;

public class SessionStatistic implements Serializable {
	
	private static final long serialVersionUID = -500983105679164242L;

	private int nrOfCards;

	private int nrOfLearned;

	private int nrOfNotAnswered;

	private int nrOfCorrect;

	private int nrOfWrong;

	public int getNrOfCards() {
		return nrOfCards;
	}

	public void setNrOfCards(int nrOfCards) {
		this.nrOfCards = nrOfCards;
	}

	public int getNrOfLearned() {
		return nrOfLearned;
	}

	public void setNrOfLearned(int nrOfLearned) {
		this.nrOfLearned = nrOfLearned;
	}

	public int getNrOfNotAnswered() {
		return nrOfNotAnswered;
	}

	public void setNrOfNotAnswered(int nrOfNotLearned) {
		this.nrOfNotAnswered = nrOfNotLearned;
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

	public void incNrOfLearned() {
		this.nrOfLearned++;
	}

	public void incNrOfNotAnswered() {
		this.nrOfNotAnswered++;
	}

	public void incNrOfCorrect() {
		this.nrOfCorrect++;
	}

	public void incNrOfWrong() {
		this.nrOfWrong++;
	}
}
