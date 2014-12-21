/**
 * Place.java
 * Dec 15, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.docschedulerdoc;

import java.util.*;

import com.parse.*;

public class Place {
	public static final String PLACE_OBJECT_KEY = "Place";

	public static final String PLACENAME_KEY = "placeName";
	public static final String DAYS_KEY = "days";
	public static final String STARTS_KEY = "starts";
	public static final String ENDS_KEY = "ends";

	private String mName;
	private List<DayTime> mDayTimes;

	public Place(String name, List<DayTime> times) {
		setName(name);
		setDayTimes(times);
	}

	public Place() {
		this("", new ArrayList<DayTime>());
	}

	public List<DayTime> getDayTimes() {
		return mDayTimes;
	}

	public void setDayTimes(List<DayTime> times) {
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
		return mName + ", " + mDayTimes.size() + " time"
				+ (mDayTimes.size() == 1 ? "" : "s") + " a week";
	}

	/**
	 * Converts this Place object to a Parse Object of type
	 * {@link Place#PLACE_OBJECT_KEY}.
	 * 
	 * @return the ParseObject
	 */
	public ParseObject getParseObject() {
		ParseObject obj = new ParseObject(PLACE_OBJECT_KEY);
		obj.put(PLACENAME_KEY, mName);
		List<String> days = new ArrayList<String>();
		List<String> starts = new ArrayList<String>();
		List<String> ends = new ArrayList<String>();
		for (DayTime t : mDayTimes) {
			days.add(t.getDay());
			starts.add(t.getStartString());
			ends.add(t.getEndString());
		}
		obj.addAll(DAYS_KEY, days);
		obj.addAll(STARTS_KEY, starts);
		obj.addAll(ENDS_KEY, ends);
		return obj;
	}

	public void construct(ParseObject o) {
		String x = o.getString("placeName");
		setName(x);
		List<String> days = o.getList(DAYS_KEY);
		List<String> starts = o.getList(STARTS_KEY);
		List<String> ends = o.getList(ENDS_KEY);
		for (int i = 0; i < days.size(); i++) {
			DayTime t = new DayTime(days.get(i), starts.get(i), ends.get(i));
			addDayTime(t);
		}
	}
}
