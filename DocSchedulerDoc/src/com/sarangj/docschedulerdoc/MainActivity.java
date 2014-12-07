package com.sarangj.docschedulerdoc;

import java.util.*;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.facebook.Session;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends FragmentActivity {
	private Fragment mFragment;

	boolean isAWS = false;

	Button createObjectBtn, signupBtn;
	EditText userText, passText;
	ParseApplication myApp;

	/*
	 * CognitoCachingCredentialsProvider mProvider = new
	 * CognitoCachingCredentialsProvider( this, AWS_ACCOUNT_ID,
	 * AWS_COGNITO_DOCIDENTITYPOOL, AWS_UNAUTHROLE, AWS_AUTHROLE,
	 * Regions.US_EAST_1);
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isAWS) {
			if (savedInstanceState == null) {
				mFragment = new LoginFragment();
				getSupportFragmentManager().beginTransaction()
						.add(android.R.id.content, mFragment).commit();
			} else {
				mFragment = (LoginFragment) getSupportFragmentManager()
						.findFragmentById(android.R.id.content);
			}
		} else {
			setContentView(R.layout.activity_parse);

			myApp = (ParseApplication) getApplication();

			createObjectBtn = (Button) findViewById(R.id.createObjectBtn);
			createObjectBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					myApp.testObjects();
				}
			});
			userText = (EditText) findViewById(R.id.username);
			passText = (EditText) findViewById(R.id.password);
			signupBtn = (Button) findViewById(R.id.signUpBtn);
			signupBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					myApp.signUp(userText.getText().toString(), passText
							.getText().toString());
				}
			});
		}
	}
}
