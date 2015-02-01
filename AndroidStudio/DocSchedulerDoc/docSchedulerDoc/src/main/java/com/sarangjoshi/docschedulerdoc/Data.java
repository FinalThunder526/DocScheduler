package com.sarangjoshi.docschedulerdoc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by Sarang on 1/7/2015.
 */
public class Data {
    public static ProgressDialog d;

    private Context mContext;
    private SharedPreferences mPref;

    public Data(Context c) {
        mContext = c;
        mPref = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    public boolean saveCurrentDoctor(String docId) {
        return mPref.edit().putString("current-doctor", docId).commit();
    }

    public String getCurrentDoctor() {
        return mPref.getString("current-doctor", null);
    }

    public boolean saveIsPatientMode(boolean isPatientMode) {
        return mPref.edit().putBoolean("patient", isPatientMode).commit();
    }

    public boolean isPatientMode() {
        return mPref.getBoolean("patient", false);
    }

    /**
     * Format: DD-MM-YYYY
     */
    public static String getTodayString() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH), month = c.get(Calendar.MONTH) + 1,
                year = c.get(Calendar.YEAR);
        String s = ((day < 10) ? "0" + day : day) + "-";
        s += ((month < 10) ? "0" + month : month) + "-";
        s += year;

        return s;
    }


}
