/**
 * MainFragment.java
 * Nov 27, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.*;
import com.amazonaws.mobileconnectors.cognito.Dataset.SyncCallback;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.regions.Regions;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

public class LoginFragment extends Fragment {
	private static final String TAG = "LoginFragment";

	public static final String AWS_ACCOUNT_ID = "597338470137";
	public static final String AWS_COGNITO_DOCIDENTITYPOOL = "us-east-1:8e74cb73-4864-4587-abc6-b10b05cf44ee";
	public static final String AWS_UNAUTHROLE = "arn:aws:iam::"
			+ AWS_ACCOUNT_ID + ":role/Cognito_DocSchedulerAuth_DefaultRole";
	public static final String AWS_AUTHROLE = "arn:aws:iam::" + AWS_ACCOUNT_ID
			+ ":role/Cognito_DocSchedulerAuth_DefaultRole";

	private TextView loginName;
	private ProfilePictureView profilePictureView;
	private GraphUser mUser;

	private boolean amazonReady = false;

	// Facebook objects
	private Session.StatusCallback mCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception e) {
			onSessionStateChanged(session, state, e);
		}
	};
	private UiLifecycleHelper mHelper;

	// Amazon objects
	CognitoCachingCredentialsProvider mCredentials;
	CognitoSyncManager mManager;

	// Database keys
	private static final String NAME_KEY = "name";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper = new UiLifecycleHelper(getActivity(), mCallback);
		mHelper.onCreate(savedInstanceState);

		new AmazonLoginTask().execute();
	}

	@Override
	public void onResume() {
		super.onResume();

		// If the main activity is launched and user session is not null
		// For example from the Facebook app
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed()))
			onSessionStateChanged(session, session.getState(), null);

		mHelper.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);
		loginName = (TextView) view.findViewById(R.id.loginName);
		profilePictureView = (ProfilePictureView) view
				.findViewById(R.id.profilePicture);
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setFragment(this);
		updateUI();
		return view;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHelper.onDestroy();
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	/**
	 * Updates the UI with the current session.
	 */
	private void updateUI() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			if (mUser != null) {
				loginName.setText(buildUserData(mUser));
				profilePictureView.setProfileId(mUser.getId());
			}
		} else {
			loginName
					.setText(getResources().getString(R.string.facebook_login));
			profilePictureView.setProfileId(null);
		}
	}

	/**
	 * Called when the current session's state is changed.
	 * 
	 * @param session
	 * @param state
	 * @param e
	 */
	private void onSessionStateChanged(Session session, SessionState state,
			Exception e) {
		if (state.isOpened()) {
			onFacebookLogin(session);
		} else if (state.isClosed()) {
			Log.i(TAG, "Session logged out");
			updateUI();
			amazonReady = false;
		}
	}

	/**
	 * Method to call when Facebook has been logged into.
	 * 
	 * @param session
	 */
	private void onFacebookLogin(Session session) {
		Log.i(TAG, "Session logged in");

		// Executes a new "me" request asynchronously.
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				mUser = user;
				updateUI();
				if (amazonReady)
					amazonFunctions();
			}
		}).executeAsync();
	}

	/**
	 * Given a GraphUser, generates the data to display.
	 */
	private String buildUserData(GraphUser user) {
		StringBuilder builder = new StringBuilder("");

		String email = (String) user.getProperty("email");

		builder.append(String.format("Name: %s\n", user.getName())).append(
				String.format("Email: %s\n", email));

		return builder.toString();
	}

	private void amazonFunctions() {
		// Amazon
		Map<String, String> logins = new HashMap<String, String>();
		logins.put("graph.facebook.com", Session.getActiveSession()
				.getAccessToken());
		if (mCredentials != null)
			mCredentials.withLogins(logins);

		Dataset dataset = mManager.openOrCreateDataset("dataset0");
		if (mUser != null) {
			String name = dataset.get(NAME_KEY);
			if (name == null) {
				dataset.put(NAME_KEY, mUser.getName());

				SyncCallback syncCallback = new MySyncCallback();
				dataset.synchronize(syncCallback);
			}
		}
	}

	private class MySyncCallback implements SyncCallback {

		@Override
		public boolean onConflict(Dataset arg0, List<SyncConflict> arg1) {
			return true;
		}

		@Override
		public boolean onDatasetDeleted(Dataset arg0, String arg1) {
			return true;
		}

		@Override
		public boolean onDatasetsMerged(Dataset arg0, List<String> arg1) {
			return false;
		}

		@Override
		public void onFailure(DataStorageException arg0) {
			Log.d(TAG, "Amazon sync failure.");
		}

		@Override
		public void onSuccess(Dataset arg0, List<Record> arg1) {
			Log.d(TAG, "Amazon sync success.");
		}

	}

	private class AmazonLoginTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;

		public void onPreExecute() {
			pd = ProgressDialog.show(getActivity(), "", "Logging into Amazon");
		}

		public Void doInBackground(Void... params) {
			mCredentials = new CognitoCachingCredentialsProvider(getActivity(),
					AWS_ACCOUNT_ID, AWS_COGNITO_DOCIDENTITYPOOL,
					AWS_UNAUTHROLE, AWS_AUTHROLE, Regions.US_EAST_1);
			mManager = new CognitoSyncManager(getActivity(), Regions.US_EAST_1,
					mCredentials);

			amazonReady = true;
			return null;
		}

		public void onPostExecute(Void result) {
			pd.dismiss();
		}
	}
}
