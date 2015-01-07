/**
 * SignupFlowActivity.java
 * Dec 8, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.docschedulerdoc;

import java.util.*;

import com.parse.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DocCreateEditProfileActivity extends Activity {
    public static final String IS_CREATE_MODE = "create-mode";
    boolean isCreate = true;

    ProgressDialog mDialog;

    TextView title;
    EditText email, password, name, phone, study, experience, expertise;

    Button createProfileBtn;
    ProgressBar isEmailValidBar;
    CheckBox publicInfoBox;
    // TextView isEmailValidTxt;

    Map<Integer, View> indexMap;

    private static final int N_OF_FIELDS = 8;
    boolean isEmailValid = false;

    // boolean[] isValid = { false, false, false, false, false, false, false };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_createedit_profile);

        indexMap = new HashMap<Integer, View>();

        isCreate = getIntent().getBooleanExtra(IS_CREATE_MODE, true);

        title = (TextView) findViewById(R.id.signupDetailsTitle);
        createProfileBtn = (Button) findViewById(R.id.createProfileBtn);
        createProfileBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                closeKeyboard();
                if (isCreate)
                    signup();
                else save();
            }
        });
        email = (EditText) findViewById(R.id.signupEmail);
        email.setText(getIntent().getStringExtra("email"));
        email.setBackgroundColor(Color.YELLOW);
        email.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isFieldValid(0)) {
                        checkIfEmailExists();
                    } else {
                        isEmailValid = false;
                        fieldValid(0, false);
                    }
                } else {
                    email.setBackgroundColor(Color.YELLOW);
                }
                // super.onFocusChange(v, hasFocus);
            }
        });
        isEmailValidBar = (ProgressBar) findViewById(R.id.isEmailValidBar);
        // isEmailValidTxt = (TextView) findViewById(R.id.isEmailValidText);
        // Form fields
        password = (EditText) findViewById(R.id.signupPassword);
        password.setText(getIntent().getStringExtra("password"));
        password.setOnFocusChangeListener(new EditTextValidListener(1));
        name = (EditText) findViewById(R.id.nameText);
        name.setOnFocusChangeListener(new EditTextValidListener(2));
        phone = (EditText) findViewById(R.id.phoneText);
        phone.setOnFocusChangeListener(new EditTextValidListener(3));
        study = (EditText) findViewById(R.id.studyText);
        study.setOnFocusChangeListener(new EditTextValidListener(4));
        experience = (EditText) findViewById(R.id.experienceText);
        experience.setOnFocusChangeListener(new EditTextValidListener(5));
        expertise = (EditText) findViewById(R.id.expertiseText);
        expertise.setOnFocusChangeListener(new EditTextValidListener(6));
        publicInfoBox = (CheckBox) findViewById(R.id.publicInfo);
        // Sets up HashMap
        indexMap.put(0, email);
        indexMap.put(1, password);
        indexMap.put(2, name);
        indexMap.put(3, phone);
        indexMap.put(4, study);
        indexMap.put(5, experience);
        indexMap.put(6, expertise);
        indexMap.put(7, publicInfoBox);

        if (!isCreate)
            setupEdit();
    }

    private void save() {
        ParseUser user = ParseUser.getCurrentUser();

        if (user != null && user.getSessionToken() != null) {
            // Step 1: Checks validity of fields
            if (!hasErrors()) {

                mDialog = ProgressDialog.show(this, "", "Saving...");

                user.setPassword(getText(password));
                user.setEmail(getText(email).toLowerCase());

                user.put("name", getText(name));
                user.put("phone", getText(phone));
                user.put("areaofstudy", getText(study));
                user.put("experience", getText(experience));
                user.put("fieldofexpertise", getText(expertise));

                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mDialog.dismiss();
                        if (e == null) {
                            // User saved
                            Toast.makeText(DocCreateEditProfileActivity.this,
                                    "Profile saved!", Toast.LENGTH_SHORT).show();
                            // Intent intent = new
                            // Intent(DocCreateEditProfileActivity.this,HomeActivity.class);
                            // startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(DocCreateEditProfileActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Fix form errors.", Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(this, "Signup error", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets up the edit profile screen.
     */
    private void setupEdit() {
        title.setText("Edit Profile");
        email.setEnabled(false);
        publicInfoBox.setVisibility(View.INVISIBLE);
        createProfileBtn.setText("Save Profile");
    }

    private class EditTextValidListener implements OnFocusChangeListener {
        private int mIndex;

        public EditTextValidListener(int i) {
            super();
            mIndex = i;
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                fieldValid(mIndex, isFieldValid(mIndex));
            } else {
                indexMap.get(mIndex).setBackgroundColor(Color.YELLOW);
            }
        }
    }

    /**
     * Checks the Parse database if a user with the given email exists.
     */
    public void checkIfEmailExists() {
        isEmailValidBar.setVisibility(View.VISIBLE);
        email.setBackgroundColor(getResources().getColor(R.color.valid_green));
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", getText(email));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) { // no error
                    isEmailValidBar.setVisibility(View.GONE);
                    isEmailValid = true;
                    fieldValid(0, users.size() == 0);
                }
            }
        });
    }

    /**
     * Changes the background of the given index depending on whether it is
     * valid or not.
     */
    private void fieldValid(int i, boolean isV) {
        try {
            if (!isV)
                indexMap.get(i).setBackgroundColor(Color.RED);
            else
                indexMap.get(i).setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {

        }
    }

    /**
     * Signup sequence.
     */
    private void signup() {
        ParseUser user = ParseUser.getCurrentUser();

        if (user == null || user.getSessionToken() == null) {
            // Step 1: Checks validity of fields
            if (!hasErrors()) {

                mDialog = ProgressDialog.show(this, "", "Signing up...");

                user = new ParseUser();
                user.setUsername(getText(email));
                user.setPassword(getText(password));
                user.setEmail(getText(email));

                user.put("name", getText(name));
                user.put("phone", getText(phone));
                user.put("areaofstudy", getText(study));
                user.put("experience", getText(experience));
                user.put("fieldofexpertise", getText(expertise));

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        mDialog.dismiss();
                        if (e == null) {
                            // User created
                            Toast.makeText(DocCreateEditProfileActivity.this,
                                    "User created!", Toast.LENGTH_SHORT).show();
                            // Intent intent = new
                            // Intent(DocCreateEditProfileActivity.this,HomeActivity.class);
                            // startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(DocCreateEditProfileActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Fix form errors.", Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(this, "Signup error", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks the signup form for errors when the Signup button is clicked.
     *
     * @return if errors exist
     */
    private boolean hasErrors() {
        boolean a = true;
        // User
        a = a && isEmailValid;
        // Others
        for (int i = 1; i < N_OF_FIELDS; i++) {
            boolean b = isFieldValid(i);
            fieldValid(i, b);
            a = a && b;
        }
        return !a;
    }

    /**
     * Given the index of a field, tells if it is valid.
     */
    private boolean isFieldValid(int index) {
        if (index < 0)
            return false;
        if (index == 0) {
            String tEmail = getText(email).trim();
            return tEmail.length() > 0 && tEmail.contains("@")
                    && tEmail.contains(".");
        } else if (index == 1) {
            return getText(password).length() >= 6;
        } else if (index < 7) {
            return getText((EditText) indexMap.get(index)).length() > 0;
        } else if (index == 7) {
            return publicInfoBox.isChecked();
        }
        return false;
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getApplicationWindowToken(), 0);
    }

    private String getText(EditText e) {
        return e.getText().toString();
    }
}
