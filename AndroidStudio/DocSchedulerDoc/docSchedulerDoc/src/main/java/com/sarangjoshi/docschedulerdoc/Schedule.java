/**
 * DocSchedule.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.docschedulerdoc;

import java.util.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.parse.*;

public class Schedule {
    public static final String PLACES_KEY = "places";
    public static final String SCHEDULE_OBJECT_KEY = "Schedule";

    private List<Place> mPlaces;
    private final Context mContext;

    private ParseObject scheduleObject;

    private boolean mInitialized;

    SaveToParseListener mListener;

    /**
     * Initializes a new Schedule object.
     *
     * @param context
     */
    public Schedule(Context context) {
        mPlaces = new ArrayList<Place>();
        mContext = context;
        mListener = (SaveToParseListener) context;
    }

    /**
     * Adds a new Place to this schedule.
     *
     * @param place new Place
     * @return index of added Place
     */
    public int addPlace(Place place) {
        mPlaces.add(place);
        return mPlaces.size() - 1;
    }

    public Place getPlace(int i) {
        return mPlaces.get(i);
    }

    public List<Place> getPlaces() {
        return mPlaces;
    }

    /**
     * Starts the saving process.<br>
     * Step 1: Create Place ParseObjects. Save.<br>
     * Step 2: Create Schedule ParseObject, tie places in as ParseRelations.
     * Save. <br>
     * Step 3: Put Schedule into User. Save.
     *
     * @return success
     */
    public boolean saveToParse() {
        savePlaces();
        return false;
    }

    /**
     * Saves the newly created Places to the server.
     */
    private void savePlaces() {
        // Creates a list of newly created Place ParseObjects
        final List<ParseObject> parsePlaces = new ArrayList<ParseObject>();
        for (Place p : mPlaces) {
            parsePlaces.add(p.getParseObject());
        }
        if (parsePlaces.size() > 0) {
            // Saves new ParseObjects to the server
            for (int i = 0; i < parsePlaces.size() - 1; i++) {
                ParseObject p = parsePlaces.get(i);
                p.saveInBackground();
            }

            parsePlaces.get(parsePlaces.size() - 1).saveInBackground(
                    new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null)
                                saveSchedule(parsePlaces);
                        }
                    });
        } else {
            saveSchedule(parsePlaces);
        }
    }

    /**
     * Now that the new Place objects have been saved to the server, deletes the
     * old Place objects.
     *
     * @param parsePlaces the new Place objects
     */
    private void saveSchedule(final List<ParseObject> parsePlaces) {
        // Check for existing schedule
        ParseObject x = ParseUser.getCurrentUser().getParseObject(
                HomeActivity.SCHEDULE_KEY);
        boolean isEdit = x != null;
        if (isEdit)
            scheduleObject = x;
        else
            scheduleObject = new ParseObject(SCHEDULE_OBJECT_KEY);

        final ParseRelation<ParseObject> placeRelation = scheduleObject
                .getRelation(PLACES_KEY);

        if (isEdit) {
            // Retrieve list of PlaceObjects
            placeRelation.getQuery().findInBackground(
                    new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> currentPlaces,
                                         ParseException e) {
                            if (e == null) {
                                if (currentPlaces.size() > 0) {
                                    // Delete each object
                                    for (int i = 0; i < currentPlaces.size() - 1; i++) {
                                        currentPlaces.get(i)
                                                .deleteInBackground();
                                    }
                                    currentPlaces.get(currentPlaces.size() - 1)
                                            .deleteInBackground(
                                                    new DeleteCallback() {
                                                        public void done(
                                                                ParseException e) {
                                                            if (e == null)
                                                                addNewPlaces(
                                                                        parsePlaces,
                                                                        placeRelation);
                                                        }
                                                    });
                                } else {
                                    // No objects to be deleted!
                                    addNewPlaces(parsePlaces, placeRelation);
                                }
                            }
                        }
                    });
        } else {
            addNewPlaces(parsePlaces, placeRelation);
        }
        /*
         * // Get current place object titles Set<String> placeIds =
		 * getCurrentPlaceIds(); // Deletes all current places for (String id :
		 * placeIds) { deleteObject(placeRelation, id); }
		 */
    }

    /**
     * Now that the old Places have been deleted from the server, adds the new
     * ones to the ParseRelation.
     *
     * @param parsePlaces   the new places
     * @param placeRelation the ParseRelation
     */
    private void addNewPlaces(List<ParseObject> parsePlaces,
                              ParseRelation<ParseObject> placeRelation) {
        // Adds new places
        for (ParseObject o : parsePlaces) {
            placeRelation.add(o);
        }
        // Saves schedule
        scheduleObject.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                saveUser();
            }
        });
    }

    /**
     * After the Schedule's ParseRelation has the Place objects added, saves the
     * Schedule object to the current User.
     */
    private void saveUser() {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("schedule", scheduleObject);
        user.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(mContext, "Schedule saved.",
                            Toast.LENGTH_LONG).show();
                    mListener.onSaveCompleted();
                }
            }
        });
    }

    private void savePlaceId(String objectId) {

    }

    public void resetPlaces() {
        mPlaces.clear();
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public void setInitialized(boolean init) {
        mInitialized = init;
    }

    /**
     * Gets the Place with the given name.
     *
     * @param name the name of the place wanted
     * @return the place
     */
    public Place getPlace(String name) {
        for(Place p : mPlaces) {
            if(p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public interface SaveToParseListener {
        public void onSaveCompleted();
    }
}