/**
 * HomeActivity.java
 * Dec 7, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.Arrays;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class HomeActivity extends Activity {
	ParseUser mUser;

	private static Schedule mSchedule;

	public static Schedule getSchedule() {
		return mSchedule;
	}

	TextView userView;
	Button logoutBtn, testBtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mUser = ParseUser.getCurrentUser();
		mSchedule = new Schedule();

		if (mUser == null || mUser.getSessionToken() == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			setContentView(R.layout.activity_home);

			userView = (TextView) findViewById(R.id.userText);
			userView.setText("Logged in as: " + mUser.getUsername());

			logoutBtn = (Button) findViewById(R.id.logoutBtn);
		}
	}

	public void onBackPressed() {
		finish();
	}

	public void logout(View v) {
		ParseUser.logOut();
		ParseUser currentUser = ParseUser.getCurrentUser();

		if (currentUser == null || currentUser.getSessionToken() == null) {
			// Successfully logged out, bitchezzz!!!!!\
			toast("Logged out!");
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	public void savePlaces(View v) {
		final ParseObject place1 = new ParseObject("Place"), place2 = new ParseObject(
				"Place");
		place1.put(Schedule.PLACENAME_KEY, "Medicare");
		place1.addAll("days", Arrays.asList(0, 1, 2, 3, 4));
		place1.addAll("starts",
				Arrays.asList("04:00", "04:00", "04:00", "04:00", "04:00"));
		place1.addAll("ends",
				Arrays.asList("05:00", "05:00", "05:00", "05:00", "05:00"));
		place1.saveInBackground();
		place2.put(Schedule.PLACENAME_KEY, "Private");
		place2.addAll("days", Arrays.asList(0, 1));
		place2.addAll("starts", Arrays.asList("05:30", "06:30"));
		place2.addAll("ends", Arrays.asList("06:30", "07:30"));
		place2.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null)
					saveSchedule(place1, place2);
			}
		});
	}

	private void saveSchedule(ParseObject... places) {
		final ParseObject mySchedule = new ParseObject("Schedule");
		ParseRelation<ParseObject> myRelation = mySchedule
				.getRelation("places");

		for (ParseObject place : places) {
			myRelation.add(place);
		}
		mySchedule.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null)
					saveUser(mySchedule);
			}
		});
	}

	private void saveUser(ParseObject mySchedule) {
		mUser.put("schedule", mySchedule);

		mUser.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null)
					toast("Saved");
				else
					toast(e.getMessage());
			}
		});
	}

	public void editSched(View v) {
		Intent intent = new Intent(this, EditScheduleActivity.class);
		startActivityForResult(intent, 0);
	}

	private void toast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			mSchedule.saveToParse(mUser);
		}
	}
}
