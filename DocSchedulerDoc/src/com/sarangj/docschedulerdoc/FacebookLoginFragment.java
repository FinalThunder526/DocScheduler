/**
 * MainFragment.java
 * Nov 27, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.TextView;

public class FacebookLoginFragment extends Fragment {
	private static final String TAG = "FacebookLoginFragment";

	private TextView loginName;

	private Session.StatusCallback mCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception e) {
			onSessionStateChanged(session, state, e);
		}
	};
	private UiLifecycleHelper mHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper = new UiLifecycleHelper(getActivity(), mCallback);
		mHelper.onCreate(savedInstanceState);
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);
		loginName = (TextView) view.findViewById(R.id.loginName);
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions("email");

		return view;
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
			Log.i(TAG, "Session logged in");

			Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						loginName.setText(buildUserData(user));
					}
				}
			}).executeAsync();
		} else if (state.isClosed()) {
			Log.i(TAG, "Session logged out");
			loginName.setText(getResources().getString(
					R.string.login_name_default));
		}
	}

	private String buildUserData(GraphUser user) {
		StringBuilder builder = new StringBuilder("");

		String email = (String) user.getProperty("email");

		builder.append(String.format("Name: %s\n", user.getName())).append(
				String.format("Email: %s\n", email));

		return builder.toString();
	}
}
