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

    public static final String EMAIL_KEY = "email",
            PASSWORD_KEY = "password",
            NAME_KEY = "name",
            PHONE_KEY = "phone",
            A_O_STUDY_KEY = "areaofstudy",
            EXP_KEY = "experience",
            F_O_EXP_KEY = "fieldofexpertise",
            DETAILS_KEY = "doc-details";

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
                save();
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
        else
            email.requestFocus();
    }

    /**
     * Sets up the edit profile screen.
     */
    private void setupEdit() {
        if (!isCreate) {
            title.setText("Edit Profile");
            email.setEnabled(false);
            password.setEnabled(false);
            publicInfoBox.setChecked(true);
            publicInfoBox.setVisibility(View.GONE);
            createProfileBtn.setText("Save Profile");

            // Fill in existing data
            String[] docDetails = getIntent().getStringArrayExtra(DETAILS_KEY);
            email.setText(docDetails[0]);
            password.setText(docDetails[1]);
            name.setText(docDetails[2]);
            phone.setText(docDetails[3]);
            study.setText(docDetails[4]);
            experience.setText(docDetails[5]);
            expertise.setText(docDetails[6]);
        }
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
        if (isCreate) {
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
    private void save() {
        ParseUser user = ParseUser.getCurrentUser();

        if ((isCreate && (user == null || user.getSessionToken() == null))
                || (!isCreate && (user != null))) {
            // Step 1: Checks validity of fields
            if (!hasErrors()) {

                mDialog = ProgressDialog.show(this, "", (isCreate) ? "Signing up..." : "Saving...");

                if (isCreate)
                    user = new ParseUser();

                user.setUsername(getText(email));
                user.setPassword(getText(password));
                user.setEmail(getText(email));

                user.put(NAME_KEY, getText(name));
                user.put(PHONE_KEY, getText(phone));
                user.put(A_O_STUDY_KEY, getText(study));
                user.put(EXP_KEY, getText(experience));
                user.put(F_O_EXP_KEY, getText(expertise));

                if (isCreate)
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                doneSavingUser();
                            else
                                Toast.makeText(DocCreateEditProfileActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
                else
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                doneSavingUser();
                            else
                                Toast.makeText(DocCreateEditProfileActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                Toast.makeText(this, "Fix form errors.", Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(this, "General error.", Toast.LENGTH_LONG).show();
        }
    }

    private void doneSavingUser() {
        mDialog.dismiss();
        Toast.makeText(DocCreateEditProfileActivity.this, "User " + ((isCreate) ?
                "created!" : "saved!"), Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Checks the signup form for errors when the Signup button is clicked.
     *
     * @return if errors exist
     */
    private boolean hasErrors() {
        boolean a = true;
        // User
        a = a && (isCreate) ? isEmailValid : true;
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

    /**
     * Cancel editing/creating profile.
     * @param v
     */
    public void cancelCreateEdit(View v) {
        finish();
    }
}
