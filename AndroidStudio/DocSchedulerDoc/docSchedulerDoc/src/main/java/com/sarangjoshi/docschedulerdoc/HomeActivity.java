/**
 * HomeActivity.java
 * Dec 7, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.docschedulerdoc;

import com.parse.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends Activity implements SetPlaceUpdateFragment.PlaceUpdateDialogListener, Schedule.SaveToParseListener {
    public static final String SCHEDULE_KEY = "schedule";
    public static ProgressDialog d;
    private static Schedule mSchedule;

    TextView userView, placesEmptyText;
    Button logoutBtn;
    ListView todaysPlacesList;

    int selectedPlace;
    List<String> todaysPlaces;
    ArrayAdapter<String> todaysPlacesAdapter;

    String today;

    /**
     * Gets the current Schedule Java object.
     */
    public static Schedule getSchedule() {
        return mSchedule;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser user = ParseUser.getCurrentUser();
        mSchedule = new Schedule(this);

        if (user == null || user.getSessionToken() == null) {
            // User not logged in
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_home);

            userView = (TextView) findViewById(R.id.userText);
            userView.setText("Logged in as: " + user.getUsername());

            logoutBtn = (Button) findViewById(R.id.logoutBtn);

            todaysPlaces = new ArrayList<String>();
            todaysPlacesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todaysPlaces);
            todaysPlacesList = (ListView) findViewById(R.id.todayList);
            todaysPlacesList.setAdapter(todaysPlacesAdapter);
            todaysPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
            placesEmptyText = (TextView) findViewById(R.id.todayEmptyText);

            retrieveSchedule();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                retrieveSchedule();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Once the Schedule Java object has been initialized, parses today's schedule into the view.
     */
    private void loadToday() {
        // Goes through all the places and sees if any are today
        today = DayTime.getStringFromInt(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        for (Place p : mSchedule.getPlaces()) {
            for (DayTime t : p.getDayTimes()) {
                if (t.getDay().equals(today)) {
                    todaysPlaces.add(p.getName() + ": " + t.getStartString() + " to " + t.getEndString());
                }
            }
        }
        if (!todaysPlaces.isEmpty()) {
            // Places not empty
            placesEmptyText.setVisibility(View.GONE);
            todaysPlacesList.setVisibility(View.VISIBLE);
            todaysPlacesAdapter.notifyDataSetChanged();
        } else {
            // Places empty
            placesEmptyText.setVisibility(View.VISIBLE);
            todaysPlacesList.setVisibility(View.GONE);
        }
        d.dismiss();
    }

    /**
     * Retrieves the current schedule from the Parse database.
     */
    private void retrieveSchedule() {
        d = ProgressDialog.show(this, "", "Loading...");
        ParseUser user = ParseUser.getCurrentUser();
        ParseObject o = user.getParseObject("schedule");
        if (o != null)
            o.fetchInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject sched, ParseException e) {
                    if (e == null)
                        initSchedule(sched);
                }
            });
        else {
            d.dismiss();
        }
    }

    /**
     * Once the Schedule ParseObject has been retrieved, actually initialize the
     * schedule into the Schedule Java object.
     *
     * @param sched
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

    public void onBackPressed() {
        finish();
    }

    /**
     * Logs the user out.
     */
    public void logout(View v) {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null || currentUser.getSessionToken() == null) {
            // Successfully logged out
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Starts the schedule edit sequence, starting the EditScheduleActivity.
     */
    public void editSched(View v) {
        Intent intent = new Intent(this, EditScheduleActivity.class);
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            d = ProgressDialog.show(this, "", "Saving...");
            mSchedule.saveToParse();
        }
    }

    private void closeKeyboard(SetPlaceUpdateFragment v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.placeUpdateEdit.getApplicationWindowToken(), 0);
    }

    // INTERFACE IMPLEMENTATION
    @Override
    public void onDialogPositiveClick(SetPlaceUpdateFragment dialog, String newUpdate) {
        closeKeyboard(dialog);
        // TODO: Save status with new update
    }

    @Override
    public void onDialogNegativeClick(SetPlaceUpdateFragment dialog) {
        closeKeyboard(dialog);
    }

    @Override
    public void onSaveCompleted() {
        d.dismiss();
        retrieveSchedule();
    }
}
