package es.uniovi.imovil.fcrtrainer.highscores;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Clase que representa una puntuación
 * 
 */
public class Highscore implements Comparable<Highscore> {

	private int mScore;
	private int mExercise;
	private Date mDate;
	private String mUserName;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm",
			Locale.getDefault());

	public Highscore(int score, int exercise, Date date, String userName) {
		mScore = score;
		mExercise = exercise;
		mDate = date;
		mUserName = userName;
	}

	public Highscore(int score, int exercise, String dateString, String userName) {
		mScore = score;
		mExercise = exercise;
		try {
			mDate = mDateFormat.parse(dateString);
		} catch (ParseException e) {
			mDate = new Date();
		}
		mUserName = userName;
	}

	public int getScore() {
		return mScore;
	}

	public void setScore(int score) {
		mScore = score;
	}

	public int getExercise() {
		return mExercise;
	}

	public void setExercise(int exercise) {
		mExercise = exercise;
	}

	public Date getDate() {
		return (Date) mDate.clone();
	}

	public String getShortDate() {
		return mDateFormat.format(mDate);
	}

	public void setDate(Date date) {
		mDate = (Date) date.clone();
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String userName) {
		mUserName = userName;
	}

	public String toString() {
		return mUserName + " " + mScore + " " + mExercise + " "
				+ mDateFormat.format(mDate);
	}

	@Override
	public int compareTo(Highscore another) {
		if (mScore - another.getScore() != 0) {
			return mScore - another.getScore();
		} else {
			return mDate.compareTo(another.getDate());
		}
	}

}
