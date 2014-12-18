package com.sarangjoshi.docschedulerpat;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Sarang on 12/18/2014.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "dWW73hZiUfeSRc3S5fkxkb8hjPiH7p519Tcq3Tja", "DTG5Zm0uoOjNaQZMqhpPd5XQiiYRE92VAdwiPXQe");
    }
}
