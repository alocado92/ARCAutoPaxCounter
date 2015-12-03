package com.example.cristina.arc_autopaxcounter;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristina on 10/19/2015.
 */
public class BluetoothFragmentDialog extends DialogFragment {

    private ProgressBar progress_bar;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> items;
    private ListView myListView;
    private AlertDialog.Builder builder;
    private AlertDialog myDialog;
    private TextView tvSearchDevices;
    private View view;
    private ARC_Bluetooth arc_bluetooth;
    private boolean isStudyFragment = false;

    public static BluetoothFragmentDialog newInstance(String param1) {
        BluetoothFragmentDialog fragment = new BluetoothFragmentDialog();
        Bundle args = new Bundle();
        args.putString("", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(R.string.bluetooth_dialog);
        myDialog = builder.create();
        arc_bluetooth = new ARC_Bluetooth(getActivity(), isStudyFragment, arrayAdapter, myDialog, myListView);
        return myDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.bluetooth_dialog, null);

        if (getArguments() != null) {
            isStudyFragment = getArguments().getBoolean("isStartStudyNull");
        }

        progress_bar = (ProgressBar) view.findViewById(R.id.progressBar);
        myListView = (ListView) view.findViewById(R.id.dialoglist);
        tvSearchDevices = (TextView) view.findViewById(R.id.tvBluetoothSearchDevices);
        progress_bar.setVisibility(View.GONE);
        tvSearchDevices.setVisibility(View.GONE);
        items = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, items);
        myListView.setAdapter(arrayAdapter);
    }

    //@Override
    public void onPause() {
        arc_bluetooth.cancelDiscovery();
        super.onPause();
    }

    @Override
    public void onResume() {
        arc_bluetooth.startScan();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        arc_bluetooth.unregisterR();
        super.onDestroy();
    }
}

