/**
 * DocSchedule.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.*;

public class DocSchedule {
	public static final String PLACENAME_KEY = "placeName";

	private List<Place> places;

	public DocSchedule() {
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

	public List<String> getPlacesAsStrings() {
		List<String> list = new ArrayList<String>();
		for (Place p : places) {
			list.add(p.toString());
		}
		return list;
	}
}
