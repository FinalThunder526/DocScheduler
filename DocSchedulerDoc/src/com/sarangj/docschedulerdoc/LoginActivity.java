package com.sarangj.docschedulerdoc;

import java.util.*;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.facebook.Session;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends FragmentActivity {
	private Fragment mFragment;

	boolean isAWS = false;

	ProgressDialog mDialog;

	Button signupBtn, loginBtn, logoutBtn;
	EditText emailText, passText;
	Button switchScreens;

	/*
	 * CognitoCachingCredentialsProvider mProvider = new
	 * CognitoCachingCredentialsProvider( this, AWS_ACCOUNT_ID,
	 * AWS_COGNITO_DOCIDENTITYPOOL, AWS_UNAUTHROLE, AWS_AUTHROLE,
	 * Regions.US_EAST_1);
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isAWS)
			if (savedInstanceState == null) {
				mFragment = new AWSLoginFragment();
				getSupportFragmentManager().beginTransaction()
						.add(android.R.id.content, mFragment).commit();
			} else {
				mFragment = (AWSLoginFragment) getSupportFragmentManager()
						.findFragmentById(android.R.id.content);
			}
		else {
			setContentView(R.layout.activity_parse_login);
			emailText = (EditText) findViewById(R.id.loginEmail);
			passText = (EditText) findViewById(R.id.loginPassword);
			loginBtn = (Button) findViewById(R.id.loginBtn);
			loginBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					closeKeyboard();
					login(emailText.getText().toString(), passText.getText()
							.toString());
				}
			});
			switchScreens = (Button) findViewById(R.id.switchToSignupBtn);
			switchScreens.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Go to Create Profile Screen
					Intent intent = new Intent(LoginActivity.this,
							SignupActivity.class);
					String email = emailText.getText().toString();
					String pass = passText.getText().toString();
					intent.putExtra("email", email);
					intent.putExtra("password", pass);
					startActivity(intent);
				}
			});
		}
	}

	/**
	 * Login sequence.
	 * 
	 * @param username
	 * @param pass
	 */
	private void login(String username, String pass) {
		ParseUser user = ParseUser.getCurrentUser();

		if (user == null || user.getSessionToken() == null) {
			mDialog = ProgressDialog.show(this, "", "Logging in...");
			ParseUser.logInInBackground(username, pass, new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					mDialog.dismiss();
					if (e != null) {
						// Error!
						Toast.makeText(LoginActivity.this, e.getMessage(),
								Toast.LENGTH_LONG).show();
					} else {
						// Logged in successfully
						Toast.makeText(LoginActivity.this, "Logged in!",
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent(LoginActivity.this,
								HomeActivity.class);
						startActivity(intent);
						finish();
					}
				}
			});
		} else {
			Toast.makeText(this,
					"User already logged in: " + user.getUsername(),
					Toast.LENGTH_LONG).show();
		}
	}

	private void closeKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(passText.getApplicationWindowToken(), 0);
	}
}
