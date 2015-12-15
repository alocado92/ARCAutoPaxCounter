package com.example.cristina.arc_autopaxcounter;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Created by Cristina on 10/18/2015.
 */
public class CreateStudyFragment extends DialogFragment {

    private AlertDialog.Builder builder;
    private AlertDialog myDialog;
    private EditText studyName;
    private Spinner route;
    private EditText vehicleType;
    private EditText vehicleCapacity;
    private EditText fileName;
    private BluetoothDevice bt;
    private OnDataPass dataPass;
    private StartStudyFragment studyFragment;
    private String items[];

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
        route = (Spinner) view.findViewById(R.id.route);
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

        RouteThread myThread = new RouteThread();
        myThread.start();

        myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = myDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button d = myDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String value = vehicleCapacity.getText().toString();

                        if (studyName.getText().toString() != null && route.getSelectedItemPosition() != 0 &&
                                vehicleType.getText().toString() != null && (!value.equals("") && Integer.parseInt(value) > 0)) {
                            if(!fileName.getText().toString().matches(".*.txt$")) {
                                String temp[] = fileName.getText().toString().split(Pattern.quote("."));
                                if(temp.length < 3) {
                                    fileName.setText(temp[0] + ".txt");
                                }
                            }
                            goToStudyFragment();
                            myDialog.dismiss();
                            if(!((MainActivity) getActivity()).getStudy_notC())
                                ((MainActivity)getActivity()).hideInstructions();
                        }
                    }
                });

                d.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        myDialog.dismiss();
                        if(((MainActivity)getActivity()).getStudy_notC())
                            ((MainActivity)getActivity()).showInstructions();
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
        bundle.putString("route", route.getSelectedItem().toString());
        bundle.putStringArray("routeList", items);
        bundle.putInt("routePosition", route.getSelectedItemPosition() - 1);
        bundle.putString("vType", vehicleType.getText().toString());
        bundle.putString("vCap", vehicleCapacity.getText().toString());
        bundle.putString("filename", fileName.getText().toString());

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

    private class RouteThread extends Thread {

        private HttpURLConnection conn;
        private static final String myURL = "http://arcinnovations.ece.uprm.edu:3000/dropdown";
        private String temp[];

        public RouteThread() {
        }

        public void run() {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    URL url = new URL(myURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);  //get input stream
                    conn.connect();

                    ArcHttpClient client = new ArcHttpClient(getActivity());
                    int response = conn.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        String result = client.get(conn);

                        //String temp[] = result.split(",");
                        items = result.split(",");
                        temp = new String[items.length+1];
                        temp[0] = "----";
                        for(int i = 0; i < items.length; i++) {
                            temp[i+1] = items[i];
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Your code to run in GUI thread here
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, temp);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                route.setAdapter(adapter);
                                route.setSelection(0);
                            }
                        });

                    } else
                        Log.d(ArcHttpClient.TAG, conn.getResponseMessage());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(conn != null)
                        conn.disconnect();
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}



