/**
 * Place.java
 * Dec 15, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.*;

public class Place {
	private String mName;
	private List<DayTime> mDayTimes;

	public Place(String name) {
		this(name, new ArrayList<DayTime>());
	}

	public Place(String name, List<DayTime> times) {
		setName(name);
		setTimes(times);
	}

	public Place() {
		this("");
	}

	public List<DayTime> getDayTimes() {
		return mDayTimes;
	}

	public void setTimes(List<DayTime> times) {
		this.mDayTimes = times;
	}

	public void addDayTime(DayTime t) {
		mDayTimes.add(t);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public int nOfDayTimes() {
		return mDayTimes.size();
	}

	public String[] getDays() {
		String[] days = new String[nOfDayTimes()];
		for (int i = 0; i < mDayTimes.size(); i++) {
			days[i] = mDayTimes.get(i).getDay();
		}
		return days;
	}

	public String[] getStarts() {
		String[] days = new String[nOfDayTimes()];
		for (int i = 0; i < mDayTimes.size(); i++) {
			days[i] = mDayTimes.get(i).getStartString();
		}
		return days;
	}

	public String[] getEnds() {
		String[] days = new String[nOfDayTimes()];
		for (int i = 0; i < mDayTimes.size(); i++) {
			days[i] = mDayTimes.get(i).getEndString();
		}
		return days;
	}

	public String toString() {
		return mName + ", " + mDayTimes.size() + " times a week";
	}
}
