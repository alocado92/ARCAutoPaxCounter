package com.example.cristina.arc_autopaxcounter;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Cristina on 11/5/2015.
 */
public class ARC_Bluetooth {

    private static final int MESSAGE_LENGTH = 12;
    private Context context;
    private boolean wasPared = false;
    private ConnectedThread connectedThread;
    private BluetoothSocket mmSocket;
    private List<BluetoothDevice> listOfDevices;
    private BluetoothAdapter bluetoothAdapter;
        private int selectedBT = -1;
        private boolean isStudyFragment;
        private ArrayAdapter<String> arrayAdapter;
        private Dialog myDialog;
        private ListView myListView;
        private MyServiceReceiver receiver;
        public static final String BROADCAST_ACTION_ACK = "Obtain ack";
        public static final String BT_ACK = "Bluetooth acknowledgement";
        public static final String BT_CLOSE = "close";
        public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  //Standard SerialPortService ID

        public ARC_Bluetooth() {
        }

        public ARC_Bluetooth(Context context, Boolean isStudyFragment, ArrayAdapter<String> arrayAdapter, Dialog btDialog, ListView myListView) {
            this.context = context;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            listOfDevices = new ArrayList<>();
            this.isStudyFragment = isStudyFragment;
            this.arrayAdapter = arrayAdapter;
            this.myDialog = btDialog;
            this.myListView = myListView;

            IntentFilter filter = new IntentFilter();
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            filter.addAction(ARC_Bluetooth.BROADCAST_ACTION_ACK);
            receiver = new MyServiceReceiver();
            context.registerReceiver(receiver, filter);
        }

        public void startScan() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            context.registerReceiver(mReceiver, filter);

            if (bluetoothAdapter != null) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices == null || pairedDevices.size() == 0) {
                    showToast("No Paired Devices Found");
                } else {
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        if(!wasPared) {
                            arrayAdapter.add(device.getName());
                            listOfDevices.add(device);
                        }
                    }
                }
            }
            bluetoothAdapter.startDiscovery();
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
                    if(!wasPared)
                        showConnected(device);

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (!listOfDevices.contains(device) && !wasPared) {
                        arrayAdapter.add(device.getName());
                        listOfDevices.add(device);
                    }

                    myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selectedBT = position;
                            bluetoothAdapter.cancelDiscovery();
                            BluetoothDevice d = listOfDevices.get(selectedBT);
                            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                            showToast("Connecting");
                            if (pairedDevices != null) {
                                if (!pairedDevices.contains(d)) {
                                    cancelDiscovery();
                                    pair(d);

                                    Set<BluetoothDevice> pairedD = bluetoothAdapter.getBondedDevices();
                                    while(!pairedD.contains(d)) {
                                        pairedD = bluetoothAdapter.getBondedDevices();
                                        wasPared = true;
                                    }
                                }
                                connect(listOfDevices.get(selectedBT));
                            }
                        }
                    });
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        showConnected(device);

                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                        showToast("Unconnected");
                    }
                }
            }
        };

    public void connect(BluetoothDevice bt) {
            ConnectThread connectThread = new ConnectThread(bt);
            connectThread.start();
        }

        private void pair(BluetoothDevice device) {
            try {
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
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }

        public void unregisterR() {
            context.unregisterReceiver(mReceiver);
        }

        public void cancelDiscovery() {
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
            }
        }

        public void showConnected(final BluetoothDevice device) {
            showToast("Connected");

            String dn = arrayAdapter.getItem(selectedBT);
            arrayAdapter.remove(dn);
            arrayAdapter.insert(dn + "\t\t" + "(Connected)", selectedBT);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    myDialog.dismiss();
                    if(((MainActivity)context).getStudy_notC())
                        ((MainActivity)context).showInstructions();

                    if(!isStudyFragment) {
                        Intent intent1 = new Intent(context, MainActivity.class);
                        intent1.putExtra("BTdevice", device);
                        context.startActivity(intent1);
                    }
                }
            }, 2000);
        }

        public BroadcastReceiver getmReceiver() {
            return mReceiver;
        }

        //=================================================Private Classes==============================================//

        /**
         * Client initiate connection
         */
        private class ConnectThread extends Thread {
            private final BluetoothDevice mmDevice;

            public ConnectThread(BluetoothDevice device) {
                bluetoothAdapter.cancelDiscovery();
                // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
                BluetoothSocket tmp = null;
                mmDevice = device;
                // Get a BluetoothSocket to connect with the given BluetoothDevice
                try {
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

                if(mmSocket != null && mmSocket.isConnected()) {
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
                        if(connectedThread != null) {
                            bytes = mmInStream.read(buffer);
                            String strReceived = new String(buffer, 0, bytes);
                            msgReceived += strReceived;
                            if(!msgReceived.equals("") && msgReceived.length() > MESSAGE_LENGTH) {
                                parseReceivedMsg(msgReceived);      //use listener to send/receive messages to StartStudy Fragment
                                msgReceived = "";
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

        private void parseReceivedMsg(String msgReceived) {
            //Send message to StartStudyFragment
            Intent localIntent = new Intent(StartStudyFragment.MyServiceReceiver.BROADCAST_BT);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.putExtra(StartStudyFragment.BT_DATA, msgReceived);
            context.sendBroadcast(localIntent);
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);   //ack
                Toast.makeText(context, "Ack sent to Central Unit", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {

            try {
                if(mmSocket != null && mmSocket.isConnected()) {
                    mmSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("MyBLUETOOTH", "Closing socket", e);
            }
        }
    }

    public class MyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(BT_ACK);
            boolean close = intent.getBooleanExtra(BT_CLOSE, false);

            if(connectedThread != null && message != null) {
                //send acknowledgement to Bluetooth
                connectedThread.write(message.getBytes());
            }

            if(connectedThread != null && close) {
                connectedThread.cancel();
            }
        }
    }

}
