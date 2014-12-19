/**
 * HomeActivity.java
 * Dec 7, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.Arrays;

import com.parse.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class HomeActivity extends Activity {
	public static final String SCHEDULE_KEY = "schedule";

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
		mSchedule = new Schedule(this);

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
			// Successfully logged out
			toast("Logged out!");
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	public void editSched(View v) {
		Intent intent = new Intent(this, EditScheduleActivity.class);
		startActivityForResult(intent, 0);
	}

	private void toast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	public static ProgressDialog d;
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			d = ProgressDialog.show(this, "", "Saving...");
			mSchedule.saveToParse();
		}
	}
}
