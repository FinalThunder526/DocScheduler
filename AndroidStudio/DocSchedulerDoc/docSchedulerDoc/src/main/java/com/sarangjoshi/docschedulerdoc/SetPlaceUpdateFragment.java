package com.sarangjoshi.docschedulerdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class SetPlaceUpdateFragment extends DialogFragment {
    private PlaceUpdateDialogListener mListener;

    public interface PlaceUpdateDialogListener {
        public void onDialogPositiveClick(SetPlaceUpdateFragment dialog, String newUpdate);

        public void onDialogNegativeClick(SetPlaceUpdateFragment dialog);
    }

    public static final String PLACE_NAME_ARG = "place-name";
    public static final String PLACE_UPDATE_ARG = "place-update";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SetPlaceUpdateFragment.
     */
    public static SetPlaceUpdateFragment newInstance(String pName, String pUpdate) {
        SetPlaceUpdateFragment fragment = new SetPlaceUpdateFragment();
        Bundle args = new Bundle();
        args.putString(PLACE_NAME_ARG, pName);
        args.putString(PLACE_UPDATE_ARG, pUpdate);
        fragment.setArguments(args);
        return fragment;
    }

    public SetPlaceUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeName = getArguments().getString(PLACE_NAME_ARG);
            placeUpdate = getArguments().getString(PLACE_UPDATE_ARG);
        }
    }

    public String placeName;
    public String placeUpdate;
    public TextView placeTitle;
    public EditText placeUpdateEdit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_placeupdate, null);
        placeTitle = (TextView) v.findViewById(R.id.dialog_placeName);
        placeUpdateEdit = (EditText) v.findViewById(R.id.dialog_placeUpdate);

        placeTitle.setText("Set update for " + placeName);
        if (placeUpdate == null || placeUpdate.trim().equals(""))
            // No update
            placeUpdateEdit.setText("");
        else
            // Pre-existent update
            placeUpdateEdit.setText(placeUpdate);


        placeUpdateEdit.requestFocus();

        builder.setTitle("Save update")
                .setView(v)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Update to be saved
                        String newUpdate = placeUpdateEdit.getText().toString();
                        mListener.onDialogPositiveClick(SetPlaceUpdateFragment.this, newUpdate);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // No save
                        mListener.onDialogNegativeClick(SetPlaceUpdateFragment.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PlaceUpdateDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
