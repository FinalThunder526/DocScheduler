/**
 * EditScheduleActivity.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.List;

import com.parse.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class EditScheduleActivity extends Activity {
	private CheckBox delete;
	private ListView placesList;

	// private List<String> placesAsStrings;
	private PlaceAdapter placesAdapter;

	private Schedule s;

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

		delete = (CheckBox) findViewById(R.id.isDeleting);
		placesList = (ListView) findViewById(R.id.placesList);
		// placesAsStrings = s.getPlacesAsStrings();
		placesAdapter = new PlaceAdapter(this, s.getPlaces());
		placesList.setAdapter(placesAdapter);
		placesList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (delete.isChecked()) {
					deletePlace(position);
				} else {
					editPlace(position);
				}
			}
		});

		// Retrieving schedule
		retrieveSchedule();
	}

	private void retrieveSchedule() {
		d = ProgressDialog.show(this, "", "Loading...");
		ParseUser user = ParseUser.getCurrentUser();
		ParseObject o = user.getParseObject("schedule");
		if (o != null)
			o.fetchInBackground(new GetCallback<ParseObject>() {
				public void done(ParseObject sched, ParseException e) {
					if (e == null)
						setupSchedule(sched);
				}
			});
		else {
			d.dismiss();
		}
	}

	ProgressDialog d;

	/**
	 * Once the Schedule ParseObject has been retrieved, actually initialize the
	 * schedule into the Schedule Java object.
	 * 
	 * @param sched
	 */
	protected void setupSchedule(ParseObject sched) {
		initSchedule(sched);
	}

	private void initSchedule(ParseObject sched) {
		s.resetPlaces();
		// Get places
		ParseRelation<ParseObject> placeRelation = sched
				.getRelation(Schedule.PLACES_KEY);
		placeRelation.getQuery().findInBackground(
				new FindCallback<ParseObject>() {
					public void done(List<ParseObject> places, ParseException e) {
						if (e == null) {
							for (ParseObject place : places) {
								Place p = new Place();
								p.construct(place);
								// savePlaceId(place.getObjectId());
								s.getPlaces().add(p);
								placesAdapter.notifyDataSetChanged();
								d.dismiss();
							}
						}
					}
				});
	}

	private void deletePlace(int pos) {
		s.getPlaces().remove(pos);
		placesAdapter.notifyDataSetChanged();
	}

	public void addPlace(View v) {
		Intent intent = new Intent(this, EditPlaceActivity.class);
		startActivityForResult(intent, ADD_CODE);
	}

	public void editPlace(int pos) {
		Intent intent = new Intent(EditScheduleActivity.this,
				EditPlaceActivity.class);
		intent.putExtra(PLACE_INDEX, pos);
		startActivityForResult(intent, EDIT_CODE);
	}

	public void saveSchedule(View v) {
		setResult(RESULT_OK);
		finish();
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
				// placesAdapter.notifyDataSetChanged();
			} else if (requestCode == EDIT_CODE) {
				// placesAdapter.notifyDataSetChanged();
			}
			placesAdapter.notifyDataSetChanged();
		}
	}
}