package com.example.cristina.arc_autopaxcounter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import im.delight.android.location.SimpleLocation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartStudyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartStudyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartStudyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final long TIME_THRESHOLD = 5000;

    public static final String HTTP_CREATE = "create";
    public static final String HTTP_EDIT = "edit";
    public static final String DIAGNOSTIC = "diagnostic";
    public static final String HTTP_STOP = "stop";

    private View myView;
    private Menu menu;
    private TextView study;
    private TextView route;
    private TextView vehicleType;
    private TextView capacity;
    private Button startB;
    private Button stopB;
    private OnFragmentInteractionListener mListener;

    private Study studyInformation;
    private SimpleLocation location;
    private Passenger passenger;
    private BluetoothDevice btDevice;
    private BluetoothAdapter bluetoothAdapter;

    private EditText studyET;
    private EditText routeET;
    private EditText vehicleTypeET;
    private EditText capacityET;
    private boolean isEdit;
    private boolean isStart;
    private ConnectedThread myThreadConnected;
    private long lastScannedTagTime = -1;
    private String lastScannedTag;

    private HashMap<String, Passenger> tableH;
    public static final String MAP_DATA = "Passengers info";
    public static final String BT_ACK = "Bluetooth acknowledgement";
    private MyServiceReceiver receiver;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartStudyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartStudyFragment newInstance(String param1, String param2) {
        StartStudyFragment fragment = new StartStudyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StartStudyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        if (savedInstanceState != null) {
            btDevice = getArguments().getParcelable("BTdevice");
            tableH = (HashMap<String, Passenger>) savedInstanceState.getSerializable("table"); //savedInstanceState.getExtras().getSerializable("table");
        } else {
            //Initialization of dummy data
            tableH = new HashMap<>();

            //Creating dummy studies
            Study study = new Study("Exp#1", "Palacio", "TR-08", 25, "3 Nov 2015", "18:56:06");
            Study study1 = new Study("Exp#2", "Terrace", "TF-65", 25, "3 Nov 2015", "23:00:59");

            String tag = "4765876987";
            String tag1 = "65858758758";
            String tag2 = "476875674642";
            String tag3 = "476869797864";

            //Creating dummy passenger list
            SimpleLocation location = new SimpleLocation(getActivity());
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            String dt = df.format(Calendar.getInstance().getTime());
            //List<Passenger> list = new ArrayList<>();
            Passenger pass = new Passenger(location.getLatitude(), location.getLongitude(), dt, location.getLatitude(),  location.getLongitude(), dt);
            Passenger pass1 = new Passenger(32358, 6768687, "4:48:59", 553543, 76980980, "22:28:12");
            Passenger pass2 = new Passenger(75768, 65768, "6:27:32", 65868, -76576586, "01:00:34");
            Passenger pass3 = new Passenger(76987, 686986987, "28:49:55", -25345323, 76586969, "3:12:12");
            //list.add(pass);
            //list.add(pass1);
            tableH.put(study.getStart_date() + ", " + study.getStart_time() + ", " + tag, pass);
            tableH.put(study.getStart_date() + ", " + study.getStart_time() + ", " + tag1, pass1);
            tableH.put(study.getStart_date() + ", " + study.getStart_time() + ", " + tag2, pass2);
            tableH.put(study.getStart_date() + ", " + study.getStart_time() + ", " + tag3, pass3);
        }

        IntentFilter filter = new IntentFilter(MyServiceReceiver.BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyServiceReceiver();
        getActivity().registerReceiver(receiver, filter);

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("table", tableH);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.study_layout, container, false);
        String sName = getArguments().getString("studyName");
        String routeName = getArguments().getString("route");
        String vT = getArguments().getString("vType");
        String vC = getArguments().getString("vCap");

        study = (TextView) myView.findViewById(R.id.tvStudyInfo);
        route = (TextView) myView.findViewById(R.id.tvRouteInfo);
        vehicleType = (TextView) myView.findViewById(R.id.tvTypeInfo);
        capacity = (TextView) myView.findViewById(R.id.tvCapInfo);
        TextView date = (TextView) myView.findViewById(R.id.tvDateInfo);
        startB = (Button) myView.findViewById(R.id.buttonStart);
        stopB = (Button) myView.findViewById(R.id.buttonStop);

        startB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startClick();
            }
        });

        stopB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopClick();
            }
        });

        studyET = (EditText) myView.findViewById(R.id.etStudyInfo);
        routeET = (EditText) myView.findViewById(R.id.etRouteInfo);
        vehicleTypeET = (EditText) myView.findViewById(R.id.etTypeInfo);
        capacityET = (EditText) myView.findViewById(R.id.etCapInfo);

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
        String dateTime = df.format(Calendar.getInstance().getTime());
        String s[] = dateTime.split(",");

        int i = 0;
        if (!vC.equals(""))
            i = Integer.parseInt(vC);

        studyInformation = new Study(sName, routeName, vT, i, s[1].trim(), s[2].trim());
        passenger = new Passenger();
        location = new SimpleLocation(this.getActivity());

        study.setText(sName);
        route.setText(routeName);
        vehicleType.setText(vT);
        capacity.setText(vC);
        date.setText(dateTime);
        isEdit = false;
        isStart = false;

        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void edit() {
        studyET.setText(study.getText());
        routeET.setText(route.getText());
        vehicleTypeET.setText(vehicleType.getText());
        capacityET.setText(capacity.getText());

        study.setVisibility(View.GONE);
        route.setVisibility(View.GONE);
        vehicleType.setVisibility(View.GONE);
        capacity.setVisibility(View.GONE);
        startB.setVisibility(View.GONE);

        studyET.setVisibility(View.VISIBLE);
        routeET.setVisibility(View.VISIBLE);
        vehicleTypeET.setVisibility(View.VISIBLE);
        capacityET.setVisibility(View.VISIBLE);

        if(isStart) {
            startB.setVisibility(View.GONE);
            stopB.setVisibility(View.VISIBLE);
        } else {
            startB.setVisibility(View.VISIBLE);
            stopB.setVisibility(View.GONE);
        }
    }

    public void save() {

        studyInformation.setName(studyET.getText().toString());
        studyInformation.setRoute(routeET.getText().toString());
        studyInformation.setType(vehicleTypeET.getText().toString());
        String capString = capacityET.getText().toString();
        int cap = 0;
        if (!capString.equals(""))
            cap = Integer.parseInt(capString);
        studyInformation.setCapacity(cap);

        study.setText(studyET.getText());
        study.setVisibility(View.VISIBLE);
        route.setText(routeET.getText());
        route.setVisibility(View.VISIBLE);
        vehicleType.setText(vehicleTypeET.getText());
        vehicleType.setVisibility(View.VISIBLE);
        capacity.setText(capacityET.getText());
        capacity.setVisibility(View.VISIBLE);
        startB.setVisibility(View.VISIBLE);

        studyET.setVisibility(View.GONE);
        studyET.setText("");
        routeET.setVisibility(View.GONE);
        routeET.setText("");
        vehicleTypeET.setVisibility(View.GONE);
        vehicleTypeET.setText("");
        capacityET.setVisibility(View.GONE);
        capacityET.setText("");

        if(isStart) {
            startB.setVisibility(View.GONE);
            stopB.setVisibility(View.VISIBLE);
        } else {
            startB.setVisibility(View.VISIBLE);
            stopB.setVisibility(View.GONE);
        }

        //Testing
        String tag = "5765876979";
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date time = Calendar.getInstance().getTime();

        AppService.preparePassengerInfo(getActivity(), tableH, location.getLatitude(), location.getLongitude(), df.format(time), tag,
                studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time());

        //AppService.prepareEditStudy(this.getActivity(), HTTP_EDIT, studyInformation.getName(), studyInformation.getRoute(), studyInformation.getType(),
        //        studyInformation.getCapacity(), studyInformation.getStart_date(), studyInformation.getStart_time());
    }

    public void cancel() {
        study.setVisibility(View.VISIBLE);
        route.setVisibility(View.VISIBLE);
        vehicleType.setVisibility(View.VISIBLE);
        capacity.setVisibility(View.VISIBLE);
        startB.setVisibility(View.VISIBLE);

        studyET.setVisibility(View.GONE);
        studyET.setText("");
        routeET.setVisibility(View.GONE);
        routeET.setText("");
        vehicleTypeET.setVisibility(View.GONE);
        vehicleTypeET.setText("");
        capacityET.setVisibility(View.GONE);
        capacityET.setText("");

        if(isStart) {
            startB.setVisibility(View.GONE);
            stopB.setVisibility(View.VISIBLE);
        } else {
            startB.setVisibility(View.VISIBLE);
            stopB.setVisibility(View.GONE);
        }
    }

    public void startClick() {
        startB.setVisibility(View.GONE);
        stopB.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).startUpdateMenu();

        //Testing
        String tag = "5765876979";
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date time = Calendar.getInstance().getTime();

        AppService.preparePassengerInfo(getActivity(), tableH, location.getLatitude(), location.getLongitude(), df.format(time), tag,
                studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time());

        /*AppService.prepareCreateStudy(this.getActivity(), HTTP_CREATE, tableH, studyInformation.getName(),
                studyInformation.getRoute(), studyInformation.getType(), studyInformation.getCapacity(),
                studyInformation.getStart_date(), studyInformation.getStart_time());*/

        //Connect to BT (obtain input/output stream)
        //ConnectThread connectThread = new ConnectThread(btDevice);
        //connectThread.start();
    }

    public void stopClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Stop Study")
                .setMessage("Data collection will stop and current study will end. Proceed?")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                               stop();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public void stop() {
        startB.setVisibility(View.VISIBLE);
        stopB.setVisibility(View.GONE);
        //myThreadConnected.cancel();       //close BT socket

        DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm:ss");
        String dateTime = df.format(Calendar.getInstance().getTime());
        String s[] = dateTime.split(",");

        studyInformation.setEnd_date(s[0].trim());
        studyInformation.setEnd_time(s[1].trim());

        AppService.prepareStopStudy(this.getActivity(), HTTP_STOP, tableH, studyInformation.getName(), studyInformation.getEnd_date(), studyInformation.getEnd_time());
        clean();
    }

    private void clean() {
        ((MainActivity)getActivity()).clean();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pencil, menu);
        if(menu != null) {
            this.menu = menu;
            if(isEdit) {
                menu.findItem(R.id.pencil_icon).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_cancel).setVisible(true);
                isEdit = false;
            } else {
                menu.findItem(R.id.pencil_icon).setVisible(true);
                menu.findItem(R.id.action_save).setVisible(false);
                menu.findItem(R.id.action_cancel).setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return false;
        } else if (id == R.id.pencil_icon) {
            this.edit();
            if(this.menu != null) {
                menu.findItem(R.id.pencil_icon).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_cancel).setVisible(true);
            }
            return true;
        } else {
            if (id == R.id.action_save) {
                this.save();
            } else if (id == R.id.action_cancel) {
                this.cancel();
            }
            if(this.menu != null) {
                menu.findItem(R.id.pencil_icon).setVisible(true);
                menu.findItem(R.id.action_save).setVisible(false);
                menu.findItem(R.id.action_cancel).setVisible(false);
            }
            return true;
        }
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void setStart(boolean start) {
        this.isStart = start;
    }

    public class MyServiceReceiver extends BroadcastReceiver {
        public static final String BROADCAST_ACTION = "Send hash map";

        @Override
        public void onReceive(Context context, Intent intent) {
            tableH = (HashMap<String, Passenger>) intent.getSerializableExtra(MAP_DATA);
            String message = intent.getStringExtra(BT_ACK);

            if(myThreadConnected != null) {
                //send acknowledgement to Bluetooth
                myThreadConnected.write(message.getBytes());
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            //mmDevice = device;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mmDevice = bluetoothAdapter.getRemoteDevice(device.getAddress());

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                //tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(BluetoothFragmentDialog.MY_UUID);
            } catch (IOException e) {
                showToast("Unable to get bluetooth socket");
            }
            mmSocket = tmp;
        }

        public void run() {

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                    //showToast("Unable to connect");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)

            myThreadConnected = new ConnectedThread(mmSocket);
            myThreadConnected.start();

            //start service when receiving or sending inf, not here
            Intent intent = new Intent(getActivity(), AppService.class);
            getActivity().startService(intent);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
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
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);

                    parseReceivedMsg(strReceived);
                    // Send the obtained bytes to the UI activity
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Bluetooth connection lost");
                    break;
                }
            }
        }

        private void parseReceivedMsg(String strReceived) {
            String tag = "5765876979";

            switch (strReceived) {
                case "TagID":  //Passenger to parcelable
                    DateFormat df = new SimpleDateFormat("HH:mm:ss");
                    Date time = Calendar.getInstance().getTime();

                    //for(int i = 0; i < 3; i ++) {
                        if(lastScannedTagTime == -1) {
                            //time = Calendar.getInstance().getTime();
                            lastScannedTagTime = time.getTime();
                            lastScannedTag = tag;
                            AppService.preparePassengerInfo(getActivity(), tableH, location.getLatitude(), location.getLongitude(), df.format(time), tag,
                                    studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time());
                        } else {
                            //time = Calendar.getInstance().getTime();
                            long diff = time.getTime() - lastScannedTagTime;
                            if(diff > TIME_THRESHOLD || !lastScannedTag.equals(tag)) {
                                lastScannedTagTime = time.getTime();
                                lastScannedTag = tag;
                                AppService.preparePassengerInfo(getActivity(), tableH, location.getLatitude(), location.getLongitude(), df.format(time), tag,
                                        studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time());
                            }
                        }
                        /*if(i == 1) {
                            tag = "34457898098454";
                        }*/
                   // }

                    break;
                case "DP":
                        AppService.prepareDiagnosticProtocol(getActivity(), DIAGNOSTIC, tableH);
                    break;
                default:

            }
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

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public String getStudyName() {
        return studyInformation.getName();
    }

    public String getDateCreated() {
        return studyInformation.getStart_date();
    }

    public String getTimeCreated() {
        return studyInformation.getStart_time();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
