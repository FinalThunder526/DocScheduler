package com.sarangjoshi.docschedulerdoc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sarang on 1/21/2015.
 */
public class ExpandablePlaceListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<Place> mPlaces;

    public ExpandablePlaceListAdapter(Context context, List<Place> places) {
        mContext = context;
        mPlaces = places;
    }

    @Override
    public int getGroupCount() {
        return mPlaces.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Place p = mPlaces.get(groupPosition);
        return p.getDayTimes().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mPlaces.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mPlaces.get(groupPosition).getDayTimes().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 20 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_weeklysched_group, null);
        }

        TextView placeName = (TextView) convertView.findViewById(R.id.patientWeeklyGroup);
        placeName.setText(mPlaces.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_weeklysched_item, null);
        }

        TextView dayTimeName = (TextView) convertView.findViewById(R.id.patientWeeklyItem);
        dayTimeName.setText(mPlaces.get(groupPosition).getDayTimes().get(childPosition).getPrettyString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
