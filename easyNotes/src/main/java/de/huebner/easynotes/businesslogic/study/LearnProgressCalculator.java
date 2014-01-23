package de.huebner.easynotes.businesslogic.study;

import java.util.Calendar;
import java.util.Date;

import de.huebner.easynotes.businesslogic.data.Card;

/**
 * Calculates the learn progress for a card.
 */
public class LearnProgressCalculator {

	public int getMaxCompartment() {
		return 9;
	}

	/**
	 * Calculates the learning progress for the given card. With this
	 * algorithm the compartment equals the nr of correct answeres.
	 * 
	 * @param lastLearned
	 * @param currentAnswer
	 * @param nrCorrectAnswers
	 * @param nrWrongAnswers
	 * @return calculated progress
	 */
	public LearnProgress calculate(Date lastLearned, int currentAnswer,
			int nrCorrectAnswers, int nrWrongAnswers) {
		int maxCompartment = getMaxCompartment();
		LearnProgress progress = new LearnProgress();

		Date referenceDate = lastLearned;

		if (currentAnswer != Card.NO_ANSWER) {
			boolean calculateNextScheduled = true;

			if (currentAnswer == Card.ANSWER_CORRECT) {
				if (nrCorrectAnswers > maxNrOfCorrectAnswers()) {
					// The card is already in the highest compartment and
					// therefore not scheduled anymore
					progress.setCompartment(maxCompartment);
					progress.setNextScheduled(null);
					calculateNextScheduled = false;
				} else {
					// Put the card in the next compartment
					progress.setCompartment(nrCorrectAnswers);
				}
			} else {
				// Card answered wrong. Put the card in the first compartment
				progress.setCompartment(0);
			}

			if (calculateNextScheduled) {
				progress.setNextScheduled(calculateNextScheduled(
						progress.getCompartment(), referenceDate));
			}
		}

		return progress;
	}

	/**
	 * Calculates the date when the card should be learned next
	 * 
	 * @param compartment
	 *            current compartment
	 * @param referenceDate
	 *            Date when the card is learned the last time. The time is added
	 *            to this date.
	 * @return calculated date
	 */
	private Date calculateNextScheduled(int compartment, Date referenceDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(referenceDate);

		if (compartment == 0) {
			// Learn immediately. Add nothing.
		} else if (compartment == 1) {
			// Learn next day
			calendar.add(Calendar.DATE, 1);
		} else if (compartment == 2) {
			// Learn in two days
			calendar.add(Calendar.DATE, 2);
		} else if (compartment == 3) {
			// Learn in one week
			calendar.add(Calendar.DATE, 7);
		} else if (compartment == 4) {
			// Learn in 12 days
			calendar.add(Calendar.DATE, 12);
		} else if (compartment == 5) {
			// Learn in one month
			calendar.add(Calendar.MONTH, 1);
		} else if (compartment == 6) {
			// Learn in 2 months
			calendar.add(Calendar.MONTH, 2);
		} else if (compartment == 7) {
			// Learn in 3 months
			calendar.add(Calendar.MONTH, 3);
		} else if (compartment == 8) {
			// Learn in 5 months
			calendar.add(Calendar.MONTH, 5);
		} else if (compartment == 9) {
			// Learn in 9 months
			calendar.add(Calendar.MONTH, 9);
		}

		return calendar.getTime();
	}

	private int maxNrOfCorrectAnswers() {
		return 9;
	}

	/**
	 * Structure for progress of learning
	 */
	public class LearnProgress {
		private int compartment;

		private Date nextScheduled;

		public int getCompartment() {
			return compartment;
		}

		public void setCompartment(int compartment) {
			this.compartment = compartment;
		}

		public Date getNextScheduled() {
			return nextScheduled;
		}

		public void setNextScheduled(Date nextScheduled) {
			this.nextScheduled = nextScheduled;
		}
	}
}
