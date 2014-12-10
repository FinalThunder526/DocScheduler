/**
 * HomeActivity.java
 * Dec 7, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class HomeActivity extends Activity {
	ParseUser mUser;

	TextView userView;
	Button logoutBtn; 

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = ParseUser.getCurrentUser();

		if (mUser == null || mUser.getSessionToken() == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			setContentView(R.layout.activity_home);

			userView = (TextView) findViewById(R.id.userText);
			userView.setText("Logged in as: " + mUser.getUsername());

			logoutBtn = (Button) findViewById(R.id.logoutBtn);
			logoutBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					logout();
				}
			});
		}
	}

	public void onBackPressed() {
		finish();
	}

	private void logout() {
		ParseUser.logOut();
		ParseUser currentUser = ParseUser.getCurrentUser();

		if (currentUser == null || currentUser.getSessionToken() == null) {
			// Successfully logged out, bitchezzz!!!!!\
			Toast.makeText(this, "Logged out!", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
