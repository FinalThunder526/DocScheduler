package com.sarangjoshi.docschedulerpat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Sarang on 12/22/2014.
 */
public class DocScheduleActivity extends Activity {
    private ParseUser mDocUser;
    private String mObjectId;

    private TextView doctorName;
    private ListView placeList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);

        mObjectId = getIntent().getStringExtra(MainActivity.DOC_ID);

        loadDoctor();
    }

    private void loadDoctor() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(mObjectId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mDocUser = parseUser;
                updateViews();
            }

        });
    }

    /**
     * Updates the views.
     */
    private void updateViews() {
        doctorName.setText(mDocUser.getString("name"));

    }
}
