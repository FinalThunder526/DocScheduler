/**
 * TimePickerDialog.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment {
	OnTimeSetListener l;

	public TimePickerFragment(OnTimeSetListener newL) {
		l = newL;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();

		return new TimePickerDialog(getActivity(), l,
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
	}
}
