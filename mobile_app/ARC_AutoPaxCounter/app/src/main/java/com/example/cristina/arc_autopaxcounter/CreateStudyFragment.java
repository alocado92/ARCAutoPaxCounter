package com.example.cristina.arc_autopaxcounter;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by Cristina on 10/18/2015.
 */
public class CreateStudyFragment extends DialogFragment {

    private AlertDialog.Builder builder;
    private AlertDialog myDialog;
    private EditText studyName;
    private EditText route;
    private EditText vehicleType;
    private EditText vehicleCapacity;
    private EditText fileName;
    private BluetoothDevice bt;
    private OnDataPass dataPass;
    private StartStudyFragment studyFragment;

    @NonNull
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_study_dialog, null);

        if (getArguments() != null) {
            bt = getArguments().getParcelable("BTdevice");
        }

        studyName = (EditText) view.findViewById(R.id.name);
        route = (EditText) view.findViewById(R.id.route);
        vehicleType = (EditText) view.findViewById(R.id.vType);
        vehicleCapacity = (EditText) view.findViewById(R.id.vCap);
        fileName = (EditText) view.findViewById(R.id.filename);
        fileName.setText(AppService.ARC_SDCARD_DEFAULT_FILENAME);

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Create Study");
        builder.setMessage("Please fill all input fields to create a new study where the vehicle capacity must be greater than zero. " +
                "The file name will be stored as a text file (.txt) in the SDcard.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        myDialog = builder.create();

        myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = myDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String value = vehicleCapacity.getText().toString();

                        if (studyName.getText().toString() != null && route.getText().toString() != null &&
                                vehicleType.getText().toString() != null && (!value.equals("") && Integer.parseInt(value) > 0)) {
                            if(!fileName.getText().toString().matches(".*.txt$")) {
                                String temp[] = fileName.getText().toString().split(Pattern.quote("."));
                                if(temp.length < 3) {
                                   fileName.setText(temp[0] + ".txt");
                                }
                            }

                            goToStudyFragment();
                            myDialog.dismiss();
                        }
                    }
                });
            }
        });

        myDialog.show();
    }

    private void goToStudyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("studyName", studyName.getText().toString());
        bundle.putString("route", route.getText().toString());
        bundle.putString("vType", vehicleType.getText().toString());
        bundle.putString("vCap", vehicleCapacity.getText().toString());
        bundle.putString("filename", fileName.getText().toString());
        bundle.putParcelable("BTdevice", bt);

        studyFragment = new StartStudyFragment();
        passData(true);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        studyFragment.setArguments(bundle);
        ft.replace(R.id.container, studyFragment);
        ft.commit();
    }

    public interface OnDataPass {
        void onDataPass(Boolean data, Fragment studyFragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataPass = (OnDataPass) activity;
    }

    public void passData(Boolean isCreated) {
        dataPass.onDataPass(isCreated, studyFragment);
    }
}



