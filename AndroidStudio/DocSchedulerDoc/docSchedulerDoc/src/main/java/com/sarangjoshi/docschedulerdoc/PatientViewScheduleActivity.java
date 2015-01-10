package com.sarangjoshi.docschedulerdoc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sarang on 12/22/2014.
 */
public class PatientViewScheduleActivity extends Activity {
    private ParseUser mDocUser;
    private ParseObject todayUpdateObj;
    private String mObjectId, today;

    private TextView doctorName, todayUpdate;
    private ListView placeListView;
    private ArrayAdapter<String> placeAdapter;
    private List<String> placeList;

    private Schedule mSchedule;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_schedule);

        mObjectId = getIntent().getStringExtra(PatientHomeActivity.DOC_ID);
        mSchedule = new Schedule(this, true);

        doctorName = (TextView) findViewById(R.id.patDoctorName);
        todayUpdate = (TextView) findViewById(R.id.patTodayUpdate);
        placeListView = (ListView) findViewById(R.id.patTodaySchedule);
        placeList = new ArrayList<>();
        placeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeList);
        placeListView.setAdapter(placeAdapter);

        Data.d = ProgressDialog.show(this, "", "Loading...");
        loadDoctor();
    }

    private void loadDoctor() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(mObjectId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mDocUser = parseUser;
                updateViews();
            }

        });
    }

    /**
     * Updates the views.
     */
    private void updateViews() {
        doctorName.setText(mDocUser.getString("name"));
        doctorName.setVisibility(View.VISIBLE);
        // load today
        retrieveSchedule();
    }

    /**
     * Retrieves the current schedule from the Parse database.
     */
    private void retrieveSchedule() {
        ParseObject o = mDocUser.getParseObject("schedule");
        if (o != null)
            o.fetchInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject sched, ParseException e) {
                    if (e == null)
                        initSchedule(sched);
                }
            });
        else {
            loadUpdate();
        }
    }

    /**
     * Once the Schedule ParseObject has been retrieved, actually initialize the
     * schedule into the Schedule Java object.
     */
    private void initSchedule(ParseObject sched) {
        mSchedule.setInitialized(false);
        mSchedule.resetPlaces();
        // Get places
        ParseRelation<ParseObject> placeRelation = sched
                .getRelation(Schedule.PLACES_KEY);
        placeRelation.getQuery().findInBackground(
                new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> places, ParseException e) {
                        if (e == null) {
                            for (ParseObject place : places) {
                                Place p = new Place();
                                p.construct(place);
                                // savePlaceId(place.getObjectId());
                                mSchedule.getPlaces().add(p);
                            }
                            mSchedule.setInitialized(true);
                            loadToday();
                        }
                    }
                });
    }

    /**
     * Once the Schedule Java object has been initialized, parses today's schedule into the view.
     */
    private void loadToday() {
        placeList.clear();

        // Goes through all the places and sees if any are today
        today = DayTime.getStringFromInt(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        for (Place p : mSchedule.getPlaces()) {
            for (DayTime t : p.getDayTimes()) {
                if (t.getDay().equals(today)) {
                    placeList.add(p.getName() + ": " + t.getStartString() + " to " + t.getEndString());
                }
            }
        }
        if (!placeList.isEmpty()) {
            // Places not empty
            //placesEmptyText.setVisibility(View.GONE);
            placeListView.setVisibility(View.VISIBLE);
            placeAdapter.notifyDataSetChanged();
        } else {
            // Places empty
            //placesEmptyText.setVisibility(View.VISIBLE);
            placeListView.setVisibility(View.GONE);
        }
        loadUpdate();
    }

    /**
     * Downloads the object.
     */
    private void loadUpdate() {
        todayUpdate.setVisibility(View.VISIBLE);
        ParseRelation<ParseObject> dayEdits = mDocUser.getRelation(DocHomeActivity.DAYEDITS_KEY);
        //ParseQuery<ParseObject> query = ParseQuery.getQuery("DayEdit");
        ParseQuery<ParseObject> query = dayEdits.getQuery();
        today = Data.getTodayString();
        query.whereEqualTo("day", today);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    if (!parseObjects.isEmpty()) {
                        // Current update exists
                        todayUpdateObj = parseObjects.get(0);
                    } else {
                        // No update
                        todayUpdateObj = null;
                    }
                    String todaysUpdate;
                    if (todayUpdateObj == null) {
                        todaysUpdate = "No update.";
                    } else {
                        todaysUpdate = (String) todayUpdateObj.get("status");
                    }
                    todayUpdate.setText(todaysUpdate);
                }
                Data.d.dismiss();
            }
        });
    }
}
