package com.example.cristina.arc_autopaxcounter;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Cristina on 10/19/2015.
 */
public class BluetoothFragmentDialog extends DialogFragment {
    public static final UUID Base = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");     //Standard SerialPortService ID
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  //Standard SerialPortService ID

    private ProgressBar progress_bar;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> items;
    private ListView myListView;
    private AlertDialog.Builder builder;
    private AlertDialog myDialog;
    private TextView tvSearchDevices;
    private BluetoothAdapter bluetoothAdapter;
    private View view;
    private int selectedBT = -1;
    private List<BluetoothDevice> listOfDevices;
    private ConnectedThread connectedThread;
    private BluetoothSocket mmSocket;

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
        return myDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.bluetooth_dialog, null);
        progress_bar = (ProgressBar) view.findViewById(R.id.progressBar);
        myListView = (ListView) view.findViewById(R.id.dialoglist);
        tvSearchDevices = (TextView) view.findViewById(R.id.tvBluetoothSearchDevices);
        progress_bar.setVisibility(View.GONE);
        tvSearchDevices.setVisibility(View.GONE);
        items = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, items);
        myListView.setAdapter(arrayAdapter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listOfDevices = new ArrayList<BluetoothDevice>();
    }


    public void startScan() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        getActivity().registerReceiver(mReceiver, filter);

        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices == null || pairedDevices.size() == 0) {
                showToast("No Paired Devices Found");
            } else {
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    //arrayAdapter.add(device.getName() + "\t" + device.getAddress());
                    arrayAdapter.add(device.getName());
                    listOfDevices.add(device);
                }
            }
        }
        bluetoothAdapter.startDiscovery();
    }

    //@Override
    public void onPause() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        this.startScan();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showToast("Enabled");
                    bluetoothAdapter.startDiscovery();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                showToast("Starting discovery");

            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                showConnected(device);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progress_bar.setVisibility(View.GONE);
                tvSearchDevices.setVisibility(View.GONE);
                myListView.setVisibility(View.VISIBLE);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (!listOfDevices.contains(device)) {
                    //arrayAdapter.add(device.getName() + "\t" + device.getAddress());
                    arrayAdapter.add(device.getName());
                    listOfDevices.add(device);
                }

                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        selectedBT = position;
                        BluetoothDevice d = listOfDevices.get(selectedBT);
                        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                        if (pairedDevices != null || pairedDevices.size() > 0) {
                            showToast("Connecting");
                            if (!pairedDevices.contains(d))
                                pair(d);
                            ConnectThread connectThread = new ConnectThread(listOfDevices.get(selectedBT));
                            connectThread.start();
                        }
                    }
                });
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    //showConnected(device);

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    showToast("Unconnected");
                }
            }
        }
    };

    private void showConnected(final BluetoothDevice device) {
        showToast("Connected");
        String dn = arrayAdapter.getItem(selectedBT);
        arrayAdapter.remove(dn);
        arrayAdapter.insert(dn + "\t\t" + "(Connected)", selectedBT);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myDialog.dismiss();
                Intent intent1 = new Intent(getActivity(), MainActivity.class);
                intent1.putExtra("BTdevice", device);
                startActivity(intent1);
            }
        }, 2000);
    }

    private void pair(BluetoothDevice device) {
        try {
            bluetoothAdapter.cancelDiscovery();
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpair(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    /**
     * Client initiate connection
     */
    private class ConnectThread extends Thread {
        //private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
            bluetoothAdapter.cancelDiscovery();
            BluetoothSocket tmp = null;
            mmDevice = device;
            int index = 0;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                //tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                showToast("Unable to get bluetooth socket");
            }
            mmSocket = tmp;
        }

        public void run() {
            int index = 0;
            while(!mmSocket.isConnected()) {
                try {
                    // Connect the device through the socket. This will block until it succeeds or throws an exception
                    mmSocket.connect();
                } catch (IOException connectException) {
                    try {
                        Log.e("", "trying fallback...");
                        mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(mmDevice.getUuids()[index].getUuid());
                        mmSocket.connect();
                        Log.e("", "Connected");
                    } catch (Exception e2) {
                        Log.e("", "Couldn't establish Bluetooth connection!");
                        index++;
                    }
                }
            }
            boolean g = mmSocket.isConnected();
            if(mmSocket != null && g) {
                connectedThread = new ConnectedThread(mmSocket);
                connectedThread.start();
            }
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread {
        //private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[100];  // buffer store for the stream
            int bytes; // bytes returned from read()
            String msgReceived = "";

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    msgReceived += strReceived;
                    if(!msgReceived.equals("") && msgReceived.length() > 11) {
                        parseReceivedMsg(msgReceived);
                        msgReceived = "";
                    }
                    //Send the obtained bytes to the UI activity
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Bluetooth connection lost");
                    break;
                }
            }
        }

        private void parseReceivedMsg(String msgReceived) {
            //Testing - quitar
            //String message = "A";
            //write(message.getBytes());

            /*String action = msgReceived.substring(0, 1);
            String data = msgReceived.substring(1);

            if(action.equals("0")) {
                //Tag ID

            } else if(action.equals("1")) {
                //Diagnostic Protocol

            }*/
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);   //ack
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

