package com.example.cristina.arc_autopaxcounter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
//import im.delight.android.location.SimpleLocation;


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
    public static final String HTTP_DISCARD = "delete";
    public static final String HTTP_VERIFY = "verify";

    private View myView;
    private Menu menu;
    private TextView study;
    private TextView route;
    private TextView vehicleType;
    private TextView capacity;
    private Button startB;
    private Button stopB;
    private TableLayout tableL;
    private TableLayout tableTitle;
    private TextView batchCount;
    private OnFragmentInteractionListener mListener;
    private Study studyInformation;
    //private SimpleLocation location;
    private LocationManager lManager;
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
    public static final String VERIFY_ROUTE = "Verify route";
    private MyServiceReceiver receiver;
    private boolean isStudyStarted;
    private boolean isDiagnostic;
    private boolean isFirstWriteToSDcard;
    private boolean sentAckBT_DP;
    private String fileName;
    private GPS myGps;

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
        lManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (savedInstanceState != null) {
            tableH = (HashMap<String, Passenger>) savedInstanceState.getSerializable("table");
            isStudyStarted = savedInstanceState.getBoolean("studyStarted");
            isDiagnostic = savedInstanceState.getBoolean("diagnostic");
            isFirstWriteToSDcard = savedInstanceState.getBoolean("firstWriteSDcard");
            sentAckBT_DP = savedInstanceState.getBoolean("sentAckBT");
            isStart = savedInstanceState.getBoolean("isStart");
            fileName = savedInstanceState.getString("fileName");

        } else {
            tableH = new HashMap<>();
            isStudyStarted = false;
            isDiagnostic = false;
            isFirstWriteToSDcard = true;
            sentAckBT_DP = false;
        }

        myGps = new GPS(getActivity());

        //location = new SimpleLocation(getActivity());
        if(!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "GPS is disabled. Please enable GPS to obtain passenger location.", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(getActivity(), "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "Latitude: " + myGps.getLatitude() + " Longitude: " + myGps.getLongitude(), Toast.LENGTH_LONG).show();
        }

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(MyServiceReceiver.BROADCAST_BT);
        filter.addAction(MyServiceReceiver.BROADCAST_ACTION);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        receiver = new MyServiceReceiver();
        getActivity().registerReceiver(receiver, filter);
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();                                  //****************deleted

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("table", tableH);
        outState.putBoolean("studyStarted", isStudyStarted);
        outState.putBoolean("diagnostic", isDiagnostic);
        outState.putBoolean("firstWriteSDcard", isFirstWriteToSDcard);
        outState.putBoolean("sentAckBT", sentAckBT_DP);
        outState.putBoolean("isStart", isStart);
        outState.putString("fileName", fileName);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.study_layout, container, false);
        String sName = getArguments().getString("studyName");
        String routeName = getArguments().getString("route");
        String vT = getArguments().getString("vType");
        String vC = getArguments().getString("vCap");
        fileName = getArguments().getString("filename");

        study = (TextView) myView.findViewById(R.id.tvStudyInfo);
        route = (TextView) myView.findViewById(R.id.tvRouteInfo);
        vehicleType = (TextView) myView.findViewById(R.id.tvTypeInfo);
        capacity = (TextView) myView.findViewById(R.id.tvCapInfo);
        TextView date = (TextView) myView.findViewById(R.id.tvDateInfo);
        startB = (Button) myView.findViewById(R.id.buttonStart);
        stopB = (Button) myView.findViewById(R.id.buttonStop);
        tableL = (TableLayout) myView.findViewById(R.id.table);
        tableTitle = (TableLayout) myView.findViewById(R.id.tableTitle);
        batchCount = (TextView) myView.findViewById(R.id.batchCount);
        this.setTableCols();

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
        study.setText(sName);
        route.setText(routeName);
        vehicleType.setText(vT);
        capacity.setText(vC);
        date.setText(dateTime);
        isEdit = false;
        isStart = false;

        AppService.prepareVerifyStudy(this.getActivity(), HTTP_VERIFY, studyInformation.getRoute());

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

        if(!isStart) {
            routeET.setText(route.getText());
            route.setVisibility(View.GONE);
            routeET.setVisibility(View.VISIBLE);
        }

        Toast.makeText(getActivity(), "Study name can only be edited in web application. ", Toast.LENGTH_SHORT).show();

        //routeET.setText(route.getText());
        vehicleTypeET.setText(vehicleType.getText());
        capacityET.setText(capacity.getText());

        //route.setVisibility(View.GONE);
        vehicleType.setVisibility(View.GONE);
        capacity.setVisibility(View.GONE);

        //routeET.setVisibility(View.VISIBLE);
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

        //studyInformation.setRoute(routeET.getText().toString());
        studyInformation.setType(vehicleTypeET.getText().toString());
        String capString = capacityET.getText().toString();
        int cap = 0;
        if (!capString.equals(""))
            cap = Integer.parseInt(capString);
        studyInformation.setCapacity(cap);

        study.setVisibility(View.VISIBLE);
        //route.setText(routeET.getText());
        route.setVisibility(View.VISIBLE);
        vehicleType.setText(vehicleTypeET.getText());
        vehicleType.setVisibility(View.VISIBLE);
        capacity.setText(capacityET.getText());
        capacity.setVisibility(View.VISIBLE);
        startB.setVisibility(View.VISIBLE);

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

        if(isStudyStarted) {
            AppService.prepareEditStudy(this.getActivity(), HTTP_EDIT, studyInformation.getName(), studyInformation.getRoute(), studyInformation.getType(),
                    studyInformation.getCapacity());
        } else {
            studyInformation.setRoute(routeET.getText().toString());
            route.setText(routeET.getText());
            routeET.setText("");
            routeET.setVisibility(View.GONE);

            AppService.prepareVerifyStudy(this.getActivity(), HTTP_VERIFY, studyInformation.getRoute());
        }
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

    private void showInvalidRouteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Invalid Route")
                .setMessage("This is an invalid route. Please enter a valid route before starting the study.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.dismiss();
                            }
                        });
        builder.create().show();
    }

    public void startClick() {
        startB.setVisibility(View.GONE);
        stopB.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).startUpdateMenu();
        isStudyStarted = true;
        isStart = true;

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

        AppService.prepareStopStudy(this.getActivity(), HTTP_STOP, tableH, studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time(),
                studyInformation.getEnd_date(), studyInformation.getEnd_time(), isFirstWriteToSDcard, fileName);
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

        /*if (id == R.id.action_settings) {
            return false;
        } else*/

        if (id == R.id.pencil_icon) {
            this.edit();
            if (this.menu != null) {
                menu.findItem(R.id.pencil_icon).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_cancel).setVisible(true);
            }
            return true;
        } else if (id == R.id.action_gps) {
            Toast.makeText(getActivity(), "Latitude: " + myGps.getLatitude() + " Longitude: " + myGps.getLongitude(), Toast.LENGTH_SHORT).show();

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

            if(toastM != null) {
                if(!toastM.equals("INVALID")) {
                    Toast.makeText(context, toastM, Toast.LENGTH_SHORT).show();
                } else {
                    showInvalidRouteDialog();
                }
            }

            if(isStudyStarted && isTable) {
                tableH = (HashMap<String, Passenger>) intent.getSerializableExtra(MAP_DATA);
                isFirstWriteToSDcard = intent.getBooleanExtra(STUDY_FIRST_WRITE, false);
                updateTable();
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

    private void setTableCols() {
        TableRow row= new TableRow(getActivity());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        row.setPadding(0, 2, 0, 0);
        row.setBackgroundColor(Color.parseColor("#808080"));
        row.setWeightSum(100);
        row.setLayoutParams(lp);

        TextView tv = new TextView(getActivity());
        TableRow.LayoutParams tvp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 20); //23
        tv.setText("Tag ID");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#ffffff"));
        tv.setBackgroundColor(Color.parseColor("#15C8EC"));
        tvp.setMargins(1, 0, 0, 1);
        tv.setPadding(5, 5, 5, 5);
        tv.setLayoutParams(tvp);
        row.addView(tv, 0);

        TextView tv1 = new TextView(getActivity());
        TableRow.LayoutParams tvp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 30); //30
        tv1.setText("Entry location");
        tv1.setGravity(Gravity.CENTER_HORIZONTAL);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextColor(Color.parseColor("#ffffff"));
        tv1.setBackgroundColor(Color.parseColor("#15C8EC"));
        tvp1.setMargins(1, 0, 0, 1);
        tv1.setPadding(5, 5, 5, 5);
        tv1.setLayoutParams(tvp1);
        row.addView(tv1, 1);

        TextView tv2 = new TextView(getActivity());
        TableRow.LayoutParams tvp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 10); //3
        tv2.setText("Entry time");
        tv2.setGravity(Gravity.CENTER_HORIZONTAL);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setTextColor(Color.parseColor("#ffffff"));
        tv2.setBackgroundColor(Color.parseColor("#15C8EC"));
        tvp2.setMargins(1, 0, 0, 1);
        tv2.setPadding(5, 5, 5, 5);
        tv2.setLayoutParams(tvp2);
        row.addView(tv2, 2);

        TextView tv3 = new TextView(getActivity());
        TableRow.LayoutParams tvp3 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 30); //39
        tv3.setText("Exit location");
        tv3.setGravity(Gravity.CENTER_HORIZONTAL);
        tv3.setTypeface(null, Typeface.BOLD);
        tv3.setTextColor(Color.parseColor("#ffffff"));
        tv3.setBackgroundColor(Color.parseColor("#15C8EC"));
        tvp3.setMargins(1, 0, 0, 1);
        tv3.setPadding(5, 5, 5, 5);
        tv3.setLayoutParams(tvp3);
        row.addView(tv3, 3);

        TextView tv4 = new TextView(getActivity());
        TableRow.LayoutParams tvp4 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 10); //5
        tv4.setText("Exit time");
        tv4.setGravity(Gravity.CENTER_HORIZONTAL);
        tv4.setTypeface(null, Typeface.BOLD);
        tv4.setTextColor(Color.parseColor("#ffffff"));
        tv4.setBackgroundColor(Color.parseColor("#15C8EC"));
        tvp4.setMargins(1, 0, 0, 1);
        tv4.setPadding(5, 5, 5, 5);
        tv4.setLayoutParams(tvp4);
        row.addView(tv4, 4);

        tableTitle.addView(row);
    }

    private void updateTable() {

        tableL.removeAllViewsInLayout();

        int batch_count = 0;
        for(String key: tableH.keySet()) {
            Passenger currentPass = tableH.get(key);
            String a[] = key.split(",");
            String passKey = a[2].trim();

            if (currentPass.getExit_time() != null)
                batch_count++;

            TableRow row = new TableRow(getActivity());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            row.setPadding(0, 2, 0, 0);
            row.setBackgroundColor(Color.parseColor("#808080"));
            row.setWeightSum(100);
            row.setLayoutParams(lp);

            TextView tv = new TextView(getActivity());
            TableRow.LayoutParams tvp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 15);
            tv.setText(passKey);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setBackgroundColor(Color.parseColor("#ffffff"));
            tvp.setMargins(1, 0, 0, 1);
            tv.setPadding(5, 5, 5, 5);
            tv.setLayoutParams(tvp);
            row.addView(tv, 0);

            TextView tv1 = new TextView(getActivity());
            TableRow.LayoutParams tvp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 24);
            tv1.setText(currentPass.getEntry_lat() + ", " + currentPass.getEntry_lon());
            tv1.setGravity(Gravity.CENTER_HORIZONTAL);
            tv1.setBackgroundColor(Color.parseColor("#ffffff"));
            tvp1.setMargins(1, 0, 0, 1);
            tv1.setPadding(5, 5, 5, 5);
            tv1.setLayoutParams(tvp1);
            row.addView(tv1, 1);

            TextView tv2 = new TextView(getActivity());
            TableRow.LayoutParams tvp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 23);
            tv2.setText(currentPass.getEntry_time());
            tv2.setGravity(Gravity.CENTER_HORIZONTAL);
            tv2.setBackgroundColor(Color.parseColor("#ffffff"));
            tvp2.setMargins(1, 0, 0, 1);
            tv2.setPadding(5, 5, 5, 5);
            tv2.setLayoutParams(tvp2);
            row.addView(tv2, 2);

            TextView tv3 = new TextView(getActivity());
            TableRow.LayoutParams tvp3 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 19);

            if(currentPass.getExit_time() == null)
                tv3.setText("----------------------------");
            else
                tv3.setText(currentPass.getExit_lat() + ", " + currentPass.getExit_lon());

            tv3.setGravity(Gravity.CENTER_HORIZONTAL);
            tv3.setBackgroundColor(Color.parseColor("#ffffff"));
            tvp3.setMargins(1, 0, 0, 1);
            tv3.setPadding(5, 5, 5, 5);
            tv3.setLayoutParams(tvp3);
            row.addView(tv3, 3);

            TextView tv4 = new TextView(getActivity());
            TableRow.LayoutParams tvp4 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 28);

            if(currentPass.getExit_time() == null)
                tv4.setText("----------");
            else
                tv4.setText(currentPass.getExit_time());

            tv4.setGravity(Gravity.CENTER_HORIZONTAL);
            tv4.setBackgroundColor(Color.parseColor("#ffffff"));
            tvp4.setMargins(1, 0, 0, 1);
            tv4.setPadding(5, 5, 5, 5);
            tv4.setLayoutParams(tvp4);
            row.addView(tv4, 4);

            tableL.addView(row);
        }

        batchCount.setText("Complete passenger batch: " + batch_count + "/" + tableH.size());
    }

    private void handleMessage(String message) {
        String action = message.substring(0, 1);
        String tag = message.substring(1);

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date time = Calendar.getInstance().getTime();

        if(action.equals("0")) {
            //Tag ID
            if(lastScannedTagTime == -1) {
                lastScannedTagTime = time.getTime();
                lastScannedTag = tag;

                AppService.preparePassengerInfo(getActivity(), tableH, myGps.getLatitude(), myGps.getLongitude(), df.format(time), tag,
                        studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time(), isFirstWriteToSDcard, fileName);

            } else {
                long diff = time.getTime() - lastScannedTagTime;
                if(diff > TIME_THRESHOLD || !lastScannedTag.equals(tag)) {
                    lastScannedTagTime = time.getTime();
                    lastScannedTag = tag;

                    AppService.preparePassengerInfo(getActivity(), tableH, myGps.getLatitude(), myGps.getLongitude(), df.format(time), tag,
                              studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time(), isFirstWriteToSDcard, fileName);
                }
            }
        } else if(action.equals("1")) {
            //Diagnostic Protocol
            isDiagnostic = true;

            if(!sentAckBT_DP) {
                //send Ack to bluetooth
                Intent localIntent = new Intent(ARC_Bluetooth.BROADCAST_ACTION_ACK);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.putExtra(ARC_Bluetooth.BT_ACK, AppService.BLUETOOTH_ACK_MESSAGE);
                getActivity().sendBroadcast(localIntent);
                sentAckBT_DP = true;
            } else {
                //AppService.prepareDiagnosticProtocol(getActivity(), DIAGNOSTIC, location.getLatitude(), location.getLongitude(), df.format(time), tag,
                //        studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time(), isDiagnostic, fileName);

                AppService.prepareDiagnosticProtocol(getActivity(), DIAGNOSTIC, myGps.getLatitude(), myGps.getLongitude(), df.format(time), tag,
                        studyInformation.getName(), studyInformation.getStart_date(), studyInformation.getStart_time(), isDiagnostic, fileName);

                sentAckBT_DP = false;
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
