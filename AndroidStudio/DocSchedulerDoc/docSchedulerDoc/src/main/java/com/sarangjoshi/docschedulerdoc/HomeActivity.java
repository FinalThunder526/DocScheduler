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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity implements SetPlaceUpdateFragment.PlaceUpdateDialogListener {
    public static final String SCHEDULE_KEY = "schedule";

    private static Schedule mSchedule;

    TextView userView;
    Button logoutBtn;
    ListView todaysPlacesList;

    public static ProgressDialog d;

    int selectedPlace;
    List<String> todaysPlaces;
    ArrayAdapter<String> todaysPlacesAdapter;

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

            loadToday();
        }
    }

    private void loadToday() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseObject schedule = user.getParseObject(SCHEDULE_KEY);
        schedule.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject schedule, ParseException e) {

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

    /**
     * Gets the current Schedule Java object.
     */
    public static Schedule getSchedule() {
        return mSchedule;
    }

    @Override
    public void onDialogPositiveClick(SetPlaceUpdateFragment dialog, String newUpdate) {
        closeKeyboard(dialog);
        // TODO: Save status with new update
    }

    @Override
    public void onDialogNegativeClick(SetPlaceUpdateFragment dialog) {
        closeKeyboard(dialog);
    }

    private void closeKeyboard(SetPlaceUpdateFragment v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.placeUpdateEdit.getApplicationWindowToken(), 0);
    }
}
