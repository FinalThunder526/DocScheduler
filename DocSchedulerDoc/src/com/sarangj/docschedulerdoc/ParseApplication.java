/**
 * ParseApplication.java
 * Dec 7, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import android.app.Application;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.parse.*;

public class ParseApplication extends Application {
	public static final String APP_ID = "dWW73hZiUfeSRc3S5fkxkb8hjPiH7p519Tcq3Tja";
	public static final String CLIENT_KEY = "DTG5Zm0uoOjNaQZMqhpPd5XQiiYRE92VAdwiPXQe";

	ParseUser mUser;
	ProgressDialog mDialog;

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, APP_ID, CLIENT_KEY);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);

		mUser = null;
	}

	public void testObjects() {
		// Test code
		ParseObject testObject = new ParseObject("TestObject2");
		testObject.put("foo", "foo-value2");
		testObject.put("foo2", 32094);
		testObject.put("baz", false);
		testObject.saveInBackground();

		Toast.makeText(this, "Test Object created", Toast.LENGTH_SHORT).show();
	}

	public void signUp(String username, String pass) {
		if (mUser == null) {
			mDialog = ProgressDialog.show(this, "Signing up...", "");

			mUser = new ParseUser();
			mUser.setUsername(username);
			mUser.setPassword(pass);

			mUser.signUpInBackground(new SignUpCallback() {
				@Override
				public void done(ParseException e) {
					mDialog.dismiss();
					if (e == null) {
						Toast.makeText(ParseApplication.this, "User created!",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ParseApplication.this, e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
}
