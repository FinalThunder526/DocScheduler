/**
 * HomeActivity.java
 * Dec 7, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.docschedulerdoc;

import java.util.Arrays;

import com.parse.*;
import com.sarangjoshi.docschedulerdoc.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class HomeActivity extends Activity {
	public static final String SCHEDULE_KEY = "schedule";

	private static Schedule mSchedule;

	TextView userView;
	Button logoutBtn, testBtn;
	public static ProgressDialog d;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ParseUser user = ParseUser.getCurrentUser();
		mSchedule = new Schedule(this);

		if (user == null || user.getSessionToken() == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			setContentView(R.layout.activity_home);

			userView = (TextView) findViewById(R.id.userText);
			userView.setText("Logged in as: " + user.getUsername());

			logoutBtn = (Button) findViewById(R.id.logoutBtn);
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
}
