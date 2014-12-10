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

	}

}
