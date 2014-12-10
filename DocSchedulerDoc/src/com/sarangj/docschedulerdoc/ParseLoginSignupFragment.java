/**
 * ParseLoginSignupFragment.java
 * Dec 7, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class ParseLoginSignupFragment extends Fragment {

	ProgressDialog mDialog;
	ParseUser mUser;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = ParseUser.getCurrentUser();
	}
}
