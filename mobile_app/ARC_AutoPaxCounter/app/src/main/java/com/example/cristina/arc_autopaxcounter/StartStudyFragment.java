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
import android.location.LocationManager;
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
    private EditText routeET;
    private EditText vehicleTypeET;
    private EditText capacityET;
    private boolean isEdit;
    private boolean isStart;
    private long lastScannedTagTime = -1;
    private String lastScannedTag;
    private HashMap<String, Passenger> tableH;
    public static final String MAP_DATA = "Passengers info";
    public static final String MAP_FLAG = "Receiving hash table";
    public static final String BT_DATA = "My bluetooth data";
    public static final String STUDY_FIRST_WRITE = "First passenger write";
    public static final String STOP_STUDY = "Stop study";
    private MyServiceReceiver receiver;
    private boolean isStudyStarted;
    private boolean isDiagnostic;
    private boolean isFirstWriteToSDcard;
    private boolean sentAckBT_DP;

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
        /*if (getArguments() != null) {
            btDevice = getArguments().getParcelable("BTdevice");
            ARC_Bluetooth arc_bluetooth = new ARC_Bluetooth(true);
        }*/

        if (savedInstanceState != null) {
            tableH = (HashMap<String, Passenger>) savedInstanceState.getSerializable("table");
            isStudyStarted = savedInstanceState.getBoolean("studyStarted");
            isDiagnostic = savedInstanceState.getBoolean("diagnostic");
            isFirstWriteToSDcard = savedInstanceState.getBoolean("firstWriteSDcard");
            sentAckBT_DP = savedInstanceState.getBoolean("sentAckBT");

        } else {
            //Initialization of dummy data
            tableH = new HashMap<>();
            isStudyStarted = false;
            isDiagnostic = false;
            isFirstWriteToSDcard = true;
            sentAckBT_DP = false;

            //Creating dummy studies
            Study study = new Study("Exp#1", "Palacio", "TR-08", 25, "3 Nov 2015", "18:56:06");

            String tag = "4765876987";
            String tag1 = "65858758758";
            String tag2 = "476875674642";
            String tag3 = "476869797864";

            SimpleLocation location = new SimpleLocation(getActivity());
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            String dt = df.format(Calendar.getInstance().getTime());

            Passenger pass0 = new Passenger(location.getLatitude(), location.getLongitude(), dt, location.getLatitude(),  location.getLongitude(), dt);
            Passenger pass1 = new Passenger(32358, 6768687, "4:48:59", 553543, 76980980, "22:28:12");
            Passenger pass2 = new Passenger(75768, 65768, "6:27:32", 65868, -76576586, "01:00:34");
            Passenger pass3 = new Passenger(76987, 686986987, "28:49:55", -25345323, 76586969, "3:12:12");
            tableH.put(study.getStart_date() + ", " + study.getStart_time() + ", " + tag, pass0);
            tableH.put(study.getStart_date() + ", " + study.getStart_time() + ", " + tag1, pass1);
            tableH.put(study.getStart_date() + ", " + study.getStart_time() + ", " + tag2, pass2);
            tableH.put(study.getStart_date() + ", " + study.getStart_time() + ", " + tag3, pass3);
        }

        LocationManager lManager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
        if(!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "GPS is disabled. Please enable GPS to obtain passenger location.", Toast.LENGTH_LONG).show();
        }

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(MyServiceReceiver.BROADCAST_BT);
        filter.addAction(MyServiceReceiver.BROADCAST_ACTION);
        receiver = new MyServiceReceiver();
        getActivity().registerReceiver(receiver, filter);

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("table", tableH);
        outState.putBoolean("studyStarted", isStudyStarted);
        outState.putBoolean("diagnostic", isDiagnostic);
        outState.putBoolean("firstWriteSDcard", isFirstWriteToSDcard);
        outState.putBoolean("sentAckBT", sentAckBT_DP);
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
        Toast.makeText(getActivity(), "Study name can only be edited in web application", Toast.LENGTH_SHORT).show();

        routeET.setText(route.getText());
        vehicleTypeET.setText(vehicleType.getText());
        capacityET.setText(capacity.getText());

        study.setVisibility(View.GONE);
        route.setVisibility(View.GONE);
        vehicleType.setVisibility(View.GONE);
        capacity.setVisibility(View.GONE);
        startB.setVisibility(View.GONE);

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

        studyInformation.setRoute(routeET.getText().toString());
        studyInformation.setType(vehicleTypeET.getText().toString());
        String capString = capacityET.getText().toString();
        int cap = 0;
        if (!capString.equals(""))
            cap = Integer.parseInt(capString);
        studyInformation.setCapacity(cap);

        study.setVisibility(View.VISIBLE);
        route.setText(routeET.getText());
        route.setVisibility(View.VISIBLE);
        vehicleType.setText(vehicleTypeET.getText());
        vehicleType.setVisibility(View.VISIBLE);
        capacity.setText(capacityET.getText());
        capacity.setVisibility(View.VISIBLE);
        startB.setVisibility(View.VISIBLE);

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

        AppService.prepareEditStudy(this.getActivity(), HTTP_EDIT, studyInformation.getName(), studyInformation.getRoute(), studyInformation.getType(),
                studyInformation.getCapacity(), studyInformation.getStart_date(), studyInformation.getStart_time());
    }

    public void cancel() {
        study.setVisibility(View.VISIBLE);
        route.setVisibility(View.VISIBLE);
        vehicleType.setVisibility(View.VISIBLE);
        capacity.setVisibility(View.VISIBLE);
        startB.setVisibility(View.VISIBLE);

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
        isStudyStarted = true;

        AppService.prepareCreateStudy(this.getActivity(), HTTP_CREATE, tableH, studyInformation.getName(),
                studyInformation.getRoute(), studyInformation.getType(), studyInformation.getCapacity(),
                studyInformation.getStart_date(), studyInformation.getStart_time());

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

        DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm:ss");
        String dateTime = df.format(Calendar.getInstance().getTime());
        String s[] = dateTime.split(",");

        studyInformation.setEnd_date(s[0].trim());
        studyInformation.setEnd_time(s[1].trim());

        AppService.prepareStopStudy(this.getActivity(), HTTP_STOP, tableH, studyInformation.getName(), studyInformation.getEnd_date(), studyInformation.getEnd_time(),
                isFirstWriteToSDcard);
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

    public String getStudyName() {
        return studyInformation.getName();
    }

    public String getDateCreated() {
        return studyInformation.getStart_date();
    }

    public String getTimeCreated() {
        return studyInformation.getStart_time();
    }


    public class MyServiceReceiver extends BroadcastReceiver {
        public static final String BROADCAST_ACTION = "Send hash map";
        public static final String BROADCAST_BT = "Obtain data";

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isTable = intent.getBooleanExtra(MAP_FLAG, false);
            boolean isStop = intent.getBooleanExtra(STOP_STUDY, false);
            String toastM = intent.getStringExtra(ArcHttpClient.TOAST_MSG);

            if(toastM != null)
                Toast.makeText(context, toastM, Toast.LENGTH_SHORT).show();

            if(isStudyStarted && isTable) {
                tableH = (HashMap<String, Passenger>) intent.getSerializableExtra(MAP_DATA);
                isFirstWriteToSDcard = intent.getBooleanExtra(STUDY_FIRST_WRITE, false);
            }

            String message = intent.getStringExtra(BT_DATA);
            if(message != null && isStudyStarted) {
                handleMessage(message);
            }

            if(isStop) {
                clean();
            }
        }
    }

    private void handleMessage(String message) {
        //String action = message.substring(0, 1);
        //String data = message.substring(1);
        String action = "1";

        String tag = message;
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date time = Calendar.getInstance().getTime();

        if(action.equals("0")) {
            //Tag ID
            if(lastScannedTagTime == -1) {
                lastScannedTagTime = time.getTime();
                lastScannedTag = tag;
                AppService.preparePassengerInfo(getActivity(), tableH, location.getLatitude(), location.getLongitude(), df.format(time), tag,
                        studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time(), isFirstWriteToSDcard);
            } else {
                long diff = time.getTime() - lastScannedTagTime;
                if(diff > TIME_THRESHOLD || !lastScannedTag.equals(tag)) {
                    lastScannedTagTime = time.getTime();
                    lastScannedTag = tag;
                    AppService.preparePassengerInfo(getActivity(), tableH, location.getLatitude(), location.getLongitude(), df.format(time), tag,
                            studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time(), isFirstWriteToSDcard);
                }
            }
        } else if(action.equals("1")) {
            //Diagnostic Protocol
            isDiagnostic = true;

            if(!sentAckBT_DP) {
                //send Ack to bluetooth
                Intent localIntent = new Intent(ARC_Bluetooth.BROADCAST_ACTION_ACK);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.putExtra(ARC_Bluetooth.BT_ACK, message);
                getActivity().sendBroadcast(localIntent);
                sentAckBT_DP = true;
            } else {
                AppService.prepareDiagnosticProtocol(getActivity(), DIAGNOSTIC, location.getLatitude(), location.getLongitude(), df.format(time), tag,
                        studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time(), isDiagnostic);
            }
        }
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
