/**
 * EditScheduleActivity.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.List;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class EditScheduleActivity extends Activity {
	private Button add, edit;
	private CheckBox delete;
	private ListView placesList;

	// private List<String> placesAsStrings;
	private PlaceAdapter placesAdapter;

	private DocSchedule s;

	public static final int ADD_CODE = 0;
	public static final int EDIT_CODE = 1;
	public static final String PLACE_INDEX = "place-name";
	public static final String DAYS = "days";
	public static final String STARTS = "starts";
	public static final String ENDS = "ends";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editschedule);

		s = HomeActivity.getSchedule();

		placesList = (ListView) findViewById(R.id.placesList);
		// placesAsStrings = s.getPlacesAsStrings();
		placesAdapter = new PlaceAdapter(this, s.getPlaces());
		placesList.setAdapter(placesAdapter);
	}

	public void addPlace(View v) {
		Intent intent = new Intent(this, EditPlaceActivity.class);
		startActivityForResult(intent, ADD_CODE);
	}

	public void editPlace(View v) {

	}

	private class PlaceAdapter extends ArrayAdapter<Place> {

		final Context mContext;

		public PlaceAdapter(Context context, List<Place> objects) {
			super(context, R.layout.layout_place, objects);
			mContext = context;
		}

		public View getView(int pos, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View v = inflater.inflate(R.layout.layout_place, parent, false);

			((TextView) v.findViewById(R.id.placeText)).setText(s.getPlace(pos)
					.toString());

			return v;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ADD_CODE) {
				// New place has been added!
				placesAdapter.notifyDataSetChanged();
			} else if (requestCode == EDIT_CODE) {

			}
		}
	}
}
