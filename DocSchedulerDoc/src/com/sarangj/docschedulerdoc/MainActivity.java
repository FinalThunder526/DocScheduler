package com.sarangj.docschedulerdoc;

import java.util.*;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.facebook.Session;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
	private FacebookLoginFragment mFragment;

	public static final String AWS_ACCOUNT_ID = "597338470137";
	public static final String AWS_COGNITO_DOCIDENTITYPOOL = "us-east-1:8e74cb73-4864-4587-abc6-b10b05cf44ee";
	public static final String AWS_UNAUTHROLE = "arn:aws:iam::"
			+ AWS_ACCOUNT_ID + ":role/Cognito_DocSchedulerAuth_DefaultRole";
	public static final String AWS_AUTHROLE = "arn:aws:iam::" + AWS_ACCOUNT_ID
			+ ":role/Cognito_DocSchedulerAuth_DefaultRole";

	/*
	 * CognitoCachingCredentialsProvider mProvider = new
	 * CognitoCachingCredentialsProvider( this, AWS_ACCOUNT_ID,
	 * AWS_COGNITO_DOCIDENTITYPOOL, AWS_UNAUTHROLE, AWS_AUTHROLE,
	 * Regions.US_EAST_1);
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			mFragment = new FacebookLoginFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, mFragment).commit();
		} else {
			mFragment = (FacebookLoginFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}
	}

	/**
	 * Attaching Facebook login details to Amazon
	 */
	private void onLoggedIn() {
		Map<String, String> logins = new HashMap<String, String>();
		logins.put("graph.facebook.com", Session.getActiveSession()
				.getAccessToken());
		//mProvider.withLogins(logins);
	}
}
