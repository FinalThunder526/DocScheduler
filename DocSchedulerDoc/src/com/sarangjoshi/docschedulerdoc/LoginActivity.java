package com.sarangjoshi.docschedulerdoc;

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
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.sarangjoshi.docschedulerdoc.R;

public class LoginActivity extends FragmentActivity {
	public static final int LOGGED_OUT = 0;
	
	private Fragment mFragment;

	boolean isAWS = false;

	ProgressDialog mDialog;

	Button signupBtn, loginBtn, fbLoginBtn;
	EditText emailText, passText;

	/*
	 * CognitoCachingCredentialsProvider mProvider = new
	 * CognitoCachingCredentialsProvider( this, AWS_ACCOUNT_ID,
	 * AWS_COGNITO_DOCIDENTITYPOOL, AWS_UNAUTHROLE, AWS_AUTHROLE,
	 * Regions.US_EAST_1);
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
			signupBtn = (Button) findViewById(R.id.signupBtn);
			signupBtn.setOnClickListener(new OnClickListener() {
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
			fbLoginBtn = (Button) findViewById(R.id.fbLoginBtn);
			fbLoginBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					loginWithFb();
				}
			});
		}
	}

	/**
	 * Login sequence for Facebook login.
	 */
	private void loginWithFb() {
		ParseFacebookUtils.logIn(this, new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
				String s = "";
				if (e != null)
					s = e.getMessage();
				else if (user.isNew()) {
					// Signup
					s = "Signup";
				} else {
					s = "Login";
				}
				Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	public void onResume() {
		super.onResume();
		ParseUser user = ParseUser.getCurrentUser();
		if (user != null && user.getSessionToken() != null) {
			// User already logged in; probably from the signup sequence.
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);

		if (requestCode == LOGGED_OUT) {
			Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
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
						startActivityForResult(intent, LOGGED_OUT);
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
