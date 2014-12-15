/**
 * TimePickerDialog.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangj.docschedulerdoc;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {
	OnTimeSetListener l;

	int hr, min;

	public TimePickerFragment(OnTimeSetListener newL, int hr, int min) {
		l = newL;
		this.hr = hr;
		this.min = min;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new TimePickerDialog(getActivity(), l, hr, min, true);
	}
}
