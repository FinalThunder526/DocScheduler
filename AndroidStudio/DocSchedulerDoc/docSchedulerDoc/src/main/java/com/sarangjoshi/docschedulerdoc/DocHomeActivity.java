/**
 * HomeActivity.java
 * Dec 7, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.docschedulerdoc;

import com.parse.*;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DocHomeActivity extends FragmentActivity implements Schedule.SaveToParseListener, SetPlaceUpdateFragment.PlaceUpdateDialogListener {
    public static final String SCHEDULE_KEY = "schedule";
    public static final String DAYEDITS_KEY = "updates";
    private static Schedule mSchedule;
    Data mData;

    TextView userView, placesEmptyText;
    Button logoutBtn, saveUpdateBtn;
    ListView todaysPlacesList;
    TextView updateText;
    ProgressBar updateProgressBar;

    List<String> todaysPlaces;
    ArrayAdapter<String> todaysPlacesAdapter;

    String today;
    String todaysUpdate = "";
    boolean isFirstUpdate = true;
    private ParseObject todayUpdateObj;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser user = ParseUser.getCurrentUser();
        mSchedule = new Schedule(this, false);
        mData = new Data(this);

        if (mData.isPatientMode()) {
            // Patient mode
            Intent intent = new Intent(this, PatientHomeActivity.class);
            startActivity(intent);
            finish();
        } else if (user == null || user.getSessionToken() == null) {
            // User not logged in
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_doc_home);

            userView = (TextView) findViewById(R.id.userText);
            userView.setText("Logged in as: " + user.getUsername());

            logoutBtn = (Button) findViewById(R.id.logoutBtn);
            saveUpdateBtn = (Button) findViewById(R.id.saveUpdateBtn);
            saveUpdateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetPlaceUpdateFragment dialog = new SetPlaceUpdateFragment();
                    dialog.update = todaysUpdate;
                    dialog.show(getSupportFragmentManager(), "SetPlaceUpdateFragment");
                }
            });
            todaysPlaces = new ArrayList<>();
            todaysPlacesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todaysPlaces);
            todaysPlacesList = (ListView) findViewById(R.id.todayList);
            todaysPlacesList.setAdapter(todaysPlacesAdapter);
            placesEmptyText = (TextView) findViewById(R.id.todayEmptyText);
            updateText = (TextView) findViewById(R.id.updateEdit);
            updateProgressBar = (ProgressBar) findViewById(R.id.updateProgressBar);

            retrieveSchedule();
        }
    }

    // INITIALIZATION SCHEDULE METHODS

    /**
     * Retrieves the current schedule from the Parse database.
     */
    private void retrieveSchedule() {
        Data.d = ProgressDialog.show(this, "", "Loading...");
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
            Data.d.dismiss();
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
        todaysPlaces.clear();

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
        getUpdateObj();
    }

    /**
     * Downloads the object.
     */
    private void getUpdateObj() {
        ParseRelation<ParseObject> dayEdits = ParseUser.getCurrentUser().getRelation(DAYEDITS_KEY);
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
                        isFirstUpdate = false;
                    } else {
                        // No update
                        todayUpdateObj = null;
                        isFirstUpdate = true;
                    }
                    if (todayUpdateObj == null) {
                        todaysUpdate = "";
                    } else {
                        todaysUpdate = (String) todayUpdateObj.get("status");
                    }
                    updateViewsLocal();
                }
                Data.d.dismiss();
            }
        });
    }

    // FUNCTIONS
    /**
     * Opens a new dialog to save update for the chosen place
     */
    private void saveUpdate() {
        updateProgressBar.setVisibility(View.VISIBLE);
        updateText.setVisibility(View.GONE);

        if (todayUpdateObj == null)
            todayUpdateObj = new ParseObject("DayEdit");
        today = Data.getTodayString();
        todayUpdateObj.put("status", todaysUpdate);
        todayUpdateObj.put("day", today);
        todayUpdateObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Links to the current user
                    saveUpdateToDoctor();
                }
            }
        });
    }

    /**
     * Now that the object has been saved, confirms that this object is a part of the relation
     * for the current user.
     */
    private void saveUpdateToDoctor() {
        if (isFirstUpdate) {
            // Need to add object to the relation.
            ParseUser user = ParseUser.getCurrentUser();
            if (user != null) {
                ParseRelation<ParseObject> allEdits = user.getRelation(DAYEDITS_KEY);
                allEdits.add(todayUpdateObj);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        isFirstUpdate = false;
                        doneSavingUpdate();
                    }
                });
            }
        } else {
            doneSavingUpdate();
        }
    }

    private void doneSavingUpdate() {
        Toast.makeText(DocHomeActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        updateViewsLocal();
    }

    /**
     * Locally updates views. Currently, just updates today's update.
     */
    private void updateViewsLocal() {
        updateProgressBar.setVisibility(View.GONE);
        updateText.setVisibility(View.VISIBLE);
        updateText.setText(todaysUpdate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                retrieveSchedule();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Starts the schedule edit sequence, starting the DocEditScheduleActivity.
     */
    public void editSched(View v) {
        Intent intent = new Intent(this, DocEditScheduleActivity.class);
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Data.d = ProgressDialog.show(this, "", "Saving...");
            mSchedule.saveToParse();
        }
    }

    // INTERFACE IMPLEMENTATION
    @Override
    public void onSaveCompleted() {
        Data.d.dismiss();
        retrieveSchedule();
    }

    @Override
    public void onDialogPositiveClick(SetPlaceUpdateFragment dialog, String newUpdate) {
        closeKeyboard(dialog);
        todaysUpdate = newUpdate;
        saveUpdate();
    }

    @Override
    public void onDialogNegativeClick(SetPlaceUpdateFragment dialog) {
        closeKeyboard(dialog);
    }

    /**
     * Closes keyboard.
     */
    private void closeKeyboard(SetPlaceUpdateFragment d) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(d.updateEdit.getApplicationWindowToken(), 0);
        // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Gets the current Schedule Java object.
     */
    public static Schedule getSchedule() {
        return mSchedule;
    }

}
