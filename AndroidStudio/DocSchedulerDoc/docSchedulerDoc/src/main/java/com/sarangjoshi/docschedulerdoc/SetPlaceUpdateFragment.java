package com.sarangjoshi.docschedulerdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SetPlaceUpdateFragment extends DialogFragment {
    private PlaceUpdateDialogListener mListener;

    public interface PlaceUpdateDialogListener {
        public void onDialogPositiveClick(SetPlaceUpdateFragment dialog, String newUpdate);

        public void onDialogNegativeClick(SetPlaceUpdateFragment dialog);
    }

    public static final String UPDATE_ARG = "update";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SetPlaceUpdateFragment.
     */
    public static SetPlaceUpdateFragment newInstance(String pUpdate) {
        SetPlaceUpdateFragment fragment = new SetPlaceUpdateFragment();
        Bundle args = new Bundle();
        args.putString(UPDATE_ARG, pUpdate);
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
            update = getArguments().getString(UPDATE_ARG);
        }
    }

    public String update;
    public EditText updateEdit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_scheduleupdate, null);
        updateEdit = (EditText) v.findViewById(R.id.dialog_update);

        if (update == null || update.trim().equals(""))
            // No preexistent update
            updateEdit.setText("");
        else
            // Pre-existent update
            updateEdit.setText(update);

        updateEdit.requestFocus();

        builder.setTitle(getResources().getString(R.string.schedule_update_title))
                .setView(v)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Update to be saved
                        String newUpdate = updateEdit.getText().toString();
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
