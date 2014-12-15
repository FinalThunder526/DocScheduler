/**
 * DocSchedule.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.*;

import com.parse.ParseUser;

public class Schedule {
	public static final String PLACENAME_KEY = "placeName";

	private List<Place> places;

	public Schedule() {
		places = new ArrayList<Place>();
	}

	public int addPlace(Place place) {
		places.add(place);
		return places.size() - 1;
	}

	public Place getPlace(int i) {
		return places.get(i);
	}

	public List<Place> getPlaces() {
		return places;
	}

	/**
	 * Saves this schedule object to the given user.
	 * 
	 * @param user
	 *            an existent Parse User, signed in.
	 * @return success
	 */
	public boolean saveToParse(ParseUser user) {
		return false;
	}
}
