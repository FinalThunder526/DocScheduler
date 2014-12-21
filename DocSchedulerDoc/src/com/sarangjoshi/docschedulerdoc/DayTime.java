/**
 * DayTime.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.docschedulerdoc;

/**
 * Keeps track of a day-time pair.
 * 
 * @author Sarang
 */
public class DayTime {
	private String mDay;
	private int mStartHour;
	private int mStartMin;
	private int mEndHour;
	private int mEndMin;

	public DayTime(String day, int sHr, int sM, int eHr, int eM) {
		setDay(day);
		setStartHour(sHr);
		setStartMin(sM);
		setEndHour(eHr);
		setEndMin(eM);
	}

	public DayTime(String day, String start, String end) {
		setDay(day);
		setStartHour(Integer.parseInt(start.substring(0, 2)));
		setStartMin(Integer.parseInt(start.substring(3)));
		setEndHour(Integer.parseInt(end.substring(0, 2)));
		setEndMin(Integer.parseInt(end.substring(3)));
	}

	public int getStartHour() {
		return mStartHour;
	}

	public void setStartHour(int startHour) {
		this.mStartHour = startHour;
	}

	public int getStartMin() {
		return mStartMin;
	}

	public void setStartMin(int startMin) {
		this.mStartMin = startMin;
	}

	public int getEndHour() {
		return mEndHour;
	}

	public void setEndHour(int endHour) {
		this.mEndHour = endHour;
	}

	public int getEndMin() {
		return mEndMin;
	}

	public void setEndMin(int endMin) {
		this.mEndMin = endMin;
	}

	public String getDay() {
		return mDay;
	}

	public void setDay(String day) {
		this.mDay = day;
	}

	public String getStartString() {
		return getTimeAsString(mStartHour, mStartMin);
	}

	public String getEndString() {
		return getTimeAsString(mEndHour, mEndMin);
	}

	public String toString() {
		return getDay() + ", " + getStartString() + " to " + getEndString();
	}

	public static String getTimeAsString(int hr, int min) {
		String s = ((hr < 10) ? "0" : "") + hr;
		s += ":" + ((min < 10) ? "0" : "") + min;
		return s;
	}
}
