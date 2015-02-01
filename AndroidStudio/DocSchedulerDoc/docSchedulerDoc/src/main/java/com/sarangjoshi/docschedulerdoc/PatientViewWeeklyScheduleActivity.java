package com.sarangjoshi.docschedulerdoc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PatientViewWeeklyScheduleActivity extends ActionBarActivity {
    ExpandableListView weeklyScheduleListView;
    ExpandablePlaceListAdapter weeklyScheduleListAdapter;

    private TextView doctorName;

    private Schedule mSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_weekly_schedule);

        mSchedule = new Schedule(this, true);
        doctorName = (TextView) findViewById(R.id.patDoctorName);

        doctorName.setText(getIntent().getStringExtra(PatientHomeActivity.DOC_NAME));

        ArrayList<String> placesAsStrings = getIntent().getStringArrayListExtra(Schedule.PLACES_AS_STRINGS_KEY);
        for(String placeAsString : placesAsStrings) {
            mSchedule.addPlace(Place.construct(placeAsString));
        }

        weeklyScheduleListView = (ExpandableListView) findViewById(R.id.patientWeekScheduleListView);
        weeklyScheduleListAdapter = new ExpandablePlaceListAdapter(this, mSchedule.getPlaces());
        weeklyScheduleListView.setAdapter(weeklyScheduleListAdapter);
        weeklyScheduleListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_view_weekly_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
