/**
 * SignupFlowActivity.java
 * Dec 8, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.List;

import com.parse.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends Activity {
	ProgressDialog mDialog;

	EditText email, password, name, phone, study, experience, expertise;
	Button createProfileBtn;
	ProgressBar isEmailValid;
	TextView isEmailValidTxt;

	boolean isValid = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parse_createprofile);

		createProfileBtn = (Button) findViewById(R.id.createProfileBtn);
		createProfileBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				signup();
			}
		});
		email = (EditText) findViewById(R.id.signupEmail);
		email.setText(getIntent().getStringExtra("email"));
		email.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					isEmailValid.setVisibility(View.VISIBLE);
					checkIfEmailExists();
				}
			}
		});
		isEmailValid = (ProgressBar) findViewById(R.id.isEmailValidBar);
		isEmailValidTxt = (TextView) findViewById(R.id.isEmailValidText);
		password = (EditText) findViewById(R.id.signupPassword);
		password.setText(getIntent().getStringExtra("password"));
		name = (EditText) findViewById(R.id.nameText);
		phone = (EditText) findViewById(R.id.phoneText);
		study = (EditText) findViewById(R.id.studyText);
		experience = (EditText) findViewById(R.id.experienceText);
		expertise = (EditText) findViewById(R.id.expertiseText);
	}

	/**
	 * Checks the Parse database if a user with the given email exists.
	 */
	public void checkIfEmailExists() {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("email", email.getText().toString());
		query.findInBackground(new FindCallback<ParseUser>() {
			public void done(List<ParseUser> users, ParseException e) {
				if (e == null) { // no error
					isEmailValidTxt.setVisibility(View.VISIBLE);
					isEmailValid.setVisibility(View.GONE);
					if (users.size() == 0) {
						isEmailValidTxt.setText("Email hasn't been used!");
						okToSignup(true);
					} else {
						isEmailValidTxt
								.setText("Email has been used. Enter a different email.");
						okToSignup(false);
					}
				}
			}
		});
	}

	private void okToSignup(boolean isValid) {
		this.isValid = isValid;
		createProfileBtn.setClickable(this.isValid);
		createProfileBtn.setEnabled(this.isValid);
	}

	/**
	 * Signup sequence.
	 */
	private void signup() {
		ParseUser user = ParseUser.getCurrentUser();

		if (user == null || user.getSessionToken() == null) {
			mDialog = ProgressDialog.show(this, "", "Signing up...");

			user = new ParseUser();
			user.setUsername(email.getText().toString());
			user.setPassword(password.getText().toString());
			user.setEmail(email.getText().toString());

			user.put("name", name.getText().toString());
			user.put("phone", phone.getText().toString());
			user.put("areaofstudy", study.getText().toString());
			user.put("experience", experience.getText().toString());
			user.put("fieldofexpertise", expertise.getText().toString());

			user.signUpInBackground(new SignUpCallback() {
				@Override
				public void done(ParseException e) {
					mDialog.dismiss();
					if (e == null) {
						// User created
						Toast.makeText(SignupActivity.this, "User created!",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(SignupActivity.this,
								HomeActivity.class);
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(SignupActivity.this, e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			Toast.makeText(this, "Signup error", Toast.LENGTH_LONG).show();
		}
	}

	private void closeKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(passText.getApplicationWindowToken(), 0);
	}
}
