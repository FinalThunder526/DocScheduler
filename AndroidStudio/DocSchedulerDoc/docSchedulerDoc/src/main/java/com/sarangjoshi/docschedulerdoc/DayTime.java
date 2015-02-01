/**
 * DayTime.java
 * Dec 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.docschedulerdoc;

/**
 * Keeps track of a day-time pair.
 *
 * @author Sarang
 */
public class DayTime implements Comparable<DayTime> {
    private String mDay;
    private int mStartHour;
    private int mStartMin;
    private int mEndHour;
    private int mEndMin;

    public DayTime(String day, int sHr, int sM, int eHr, int eM) {
        setDay(day);
        setStartHour(sHr);
        setStartMin(sM);
        setEndHour(eHr);
        setEndMin(eM);
    }

    public DayTime(String day, String start, String end) {
        construct(day, start, end);
    }

    public void construct(String day, String start, String end) {
        setDay(day);
        setStartHour(Integer.parseInt(start.substring(0, 2)));
        setStartMin(Integer.parseInt(start.substring(3)));
        setEndHour(Integer.parseInt(end.substring(0, 2)));
        setEndMin(Integer.parseInt(end.substring(3)));
    }

    public DayTime(String s) {
        String[] parts = s.split("-");
        // day 0, start 1, end 2
        construct(parts[0], parts[1], parts[2]);
    }

    public int getStartHour() {
        return mStartHour;
    }

    public void setStartHour(int startHour) {
        this.mStartHour = startHour;
    }

    public int getStartMin() {
        return mStartMin;
    }

    public void setStartMin(int startMin) {
        this.mStartMin = startMin;
    }

    public int getEndHour() {
        return mEndHour;
    }

    public void setEndHour(int endHour) {
        this.mEndHour = endHour;
    }

    public int getEndMin() {
        return mEndMin;
    }

    public void setEndMin(int endMin) {
        this.mEndMin = endMin;
    }

    public String getDay() {
        return mDay;
    }

    public void setDay(String day) {
        this.mDay = day;
    }

    public String getStartString() {
        return getTimeAsString(mStartHour, mStartMin);
    }

    public String getEndString() {
        return getTimeAsString(mEndHour, mEndMin);
    }

    /**
     * Gets this DayTime in a user-friendly String format.
     *
     * @return
     */
    public String getPrettyString() {
        return getDay() + ", " + getStartString() + " to " + getEndString();
    }

    public String toString() {
        return getDay() + "-" + getStartString() + "-" + getEndString();
    }

    // STATIC METHODS
    public static String getTimeAsString(int hr, int min) {
        String s = ((hr < 10) ? "0" : "") + hr;
        s += ":" + ((min < 10) ? "0" : "") + min;
        return s;
    }

    public static String getDayFromInt(int x) {
        switch (x) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "ERROR";
        }
    }

    public static int getIntFromDay(String s) {
        switch (s) {
            case "Sunday":
                return 1;
            case "Monday":
                return 2;
            case "Tuesday":
                return 3;
            case "Wednesday":
                return 4;
            case "Thursday":
                return 5;
            case "Friday":
                return 6;
            case "Saturday":
                return 7;
            default:
                return -1;
        }
    }

    // this - other
    @Override
    public int compareTo(DayTime other) {
        int dayDifference = DayTime.getIntFromDay(mDay) - DayTime.getIntFromDay(other.mDay);
        if (dayDifference != 0)
            return dayDifference;
        else {
            int startDifference = (mStartHour * 60 + mStartMin)
                    - (other.mStartHour * 60 + other.mStartMin);
            if (startDifference != 0) {
                return startDifference;
            } else {
                return (mEndHour * 60 + mEndMin)
                        - (other.mEndHour * 60 + other.mEndMin);
            }
        }
    }
}
