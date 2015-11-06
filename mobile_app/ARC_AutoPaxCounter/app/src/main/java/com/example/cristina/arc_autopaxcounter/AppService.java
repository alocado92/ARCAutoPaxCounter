package com.example.cristina.arc_autopaxcounter;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AppService extends IntentService {

    //ACTIONS
    private static final String PASS_INFO = "Obtain passenger info";
    private static final String DISCARD_STUDY = "Discard study locally and remotely";
    private static final String EDIT_STUDY = "Edit study locally and remotely";
    private static final String CREATE_STUDY = "Create study locally and remotely";
    private static final String STOP_STUDY = "Stop study locally and send data to web app";
    private static final String RUN_DIAGNOSTIC = "Run diagnostic protocol";

    private static final String BLUETOOTH_ACK_MESSAGE = "A";
    private static final int BATCH_SIZE = 5;

    public AppService() {
        super("AppService");
    }

    /**
     * Starts this service to perform this action with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void prepareDiscardStudy(Context context, String action, String studyName, String dateCreated, String timeCreated) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(DISCARD_STUDY);
        intent.putExtra(MainActivity.HTTP_DISCARD, action);
        intent.putExtra("studyName", studyName);
        intent.putExtra("dateCreated", dateCreated);
        intent.putExtra("timeCreated", timeCreated);
        context.startService(intent);
    }

    public static void prepareEditStudy(Context context, String action, String studyName, String route, String type, int capacity,
                                        String dateCreated, String timeCreated) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(EDIT_STUDY);
        intent.putExtra(StartStudyFragment.HTTP_EDIT, action);
        intent.putExtra("studyName", studyName);
        intent.putExtra("route", route);
        intent.putExtra("type", type);
        intent.putExtra("capacity", capacity);
        intent.putExtra("dateCreated", dateCreated);    //to look for keys in hashmap
        intent.putExtra("timeCreated", timeCreated);    //to look for keys in hashmap
        context.startService(intent);
    }

    public static void prepareCreateStudy(Context context, String action, HashMap<String, Passenger> tableH, String name, String route,
                                          String type, int capacity, String date_start, String time_start) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(CREATE_STUDY);
        intent.putExtra(StartStudyFragment.HTTP_CREATE, action);
        intent.putExtra("table", tableH);
        intent.putExtra("studyName", name);
        intent.putExtra("route", route);
        intent.putExtra("type", type);
        intent.putExtra("capacity", capacity);
        intent.putExtra("dateStart", date_start);
        intent.putExtra("timeStart", time_start);
        context.startService(intent);
    }

    public static void prepareStopStudy(Context context, String action, HashMap<String, Passenger> tableH, String name, String dateEnd, String timeEnd) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(STOP_STUDY);
        intent.putExtra(StartStudyFragment.HTTP_STOP, action);
        intent.putExtra("table", tableH);
        intent.putExtra("studyName", name);
        intent.putExtra("dateEnd", dateEnd);
        intent.putExtra("timeEnd", timeEnd);
        context.startService(intent);
    }

    public static void preparePassengerInfo(Context context, HashMap<String, Passenger> tableH, double lat, double longi, String time, String tag, String studyName,
                                            String studyStartDate, String studyStartTime) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(PASS_INFO);
        intent.putExtra("table", tableH);
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", longi);
        intent.putExtra("time", time);
        intent.putExtra("tag", tag);
        intent.putExtra("studyName", studyName);
        intent.putExtra("studyStartDate", studyStartDate);
        intent.putExtra("studyStartTime", studyStartTime);
        context.startService(intent);
    }

    public static void prepareDiagnosticProtocol(Context context, String action, HashMap<String, Passenger> tableH) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(RUN_DIAGNOSTIC);
        intent.putExtra("table", tableH);
        intent.putExtra(StartStudyFragment.DIAGNOSTIC, action);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (PASS_INFO.equals(action)) {
                HashMap<String, Passenger> tableH = (HashMap<String, Passenger>) intent.getSerializableExtra("table");
                double lat = bundle.getDouble("latitude");
                double longi = bundle.getDouble("longitude");
                String time = bundle.getString("time");
                String tag = bundle.getString("tag");
                String studyName = bundle.getString("studyName");
                String studyStartDate = bundle.getString("studyStartDate");
                String studyStartTime = bundle.getString("studyStartTime");
                handleAction_PassengerInfo(tableH, lat, longi, time, tag, studyName, studyStartDate, studyStartTime);
            } else if (DISCARD_STUDY.equals(action)) {
                String actionHTTP = bundle.getString(MainActivity.HTTP_DISCARD);
                String studyName = bundle.getString("studyName");
                String dateEntry = bundle.getString("dateCreated");
                String timeEntry = bundle.getString("timeCreated");
                handleAction_DiscardStudy(actionHTTP, studyName, dateEntry, timeEntry);
            } else if (EDIT_STUDY.equals(action)) {
                String actionHTTP = bundle.getString(StartStudyFragment.HTTP_EDIT);
                String studyName = bundle.getString("studyName");
                String route = bundle.getString("route");
                String type = bundle.getString("type");
                int capacity = bundle.getInt("capacity");
                String dateCreated = bundle.getString("dateCreated");
                String timeCreated = bundle.getString("timeCreated");
                handleAction_EditStudy(actionHTTP, studyName, route, type, capacity, dateCreated, timeCreated);
            } else if (CREATE_STUDY.equals(action)) {
                String actionHTTP = bundle.getString(StartStudyFragment.HTTP_CREATE);
                String studyName = bundle.getString("studyName");
                String route = bundle.getString("route");
                String type = bundle.getString("type");
                int capacity = bundle.getInt("capacity");
                String dateStart = bundle.getString("dateStart");
                String timeStart = bundle.getString("timeStart");
                handleAction_CreateStudy(actionHTTP, studyName, route, type, capacity, dateStart, timeStart);
            } else if (STOP_STUDY.equals(action)) {
                String actionHTTP = bundle.getString(StartStudyFragment.HTTP_STOP);
                HashMap<String, Passenger> tableH = (HashMap<String, Passenger>) intent.getSerializableExtra("table");
                String studyName = bundle.getString("studyName");
                String dateEnd = bundle.getString("dateEnd");
                String timeEnd = bundle.getString("timeEnd");
                handleAction_StopStudy(tableH, actionHTTP, studyName, dateEnd, timeEnd);
            } else if (RUN_DIAGNOSTIC.equals(action)) {
                String actionHTTP = bundle.getString(StartStudyFragment.DIAGNOSTIC);
                HashMap<String, Passenger> tableH = (HashMap<String, Passenger>) intent.getSerializableExtra("table");
                handleAction_Diagnostic(tableH, actionHTTP);
            }
        }
    }


    private void handleAction_PassengerInfo(HashMap<String, Passenger> tableH, double lat, double longi, String time, String tag, String studyName, String studyStartDate, String studyStartTime) {

        HashMap<String, Passenger> tmp = tableH;

        Passenger passenger = tmp.get(studyStartDate + ", " + studyStartTime + ", " + tag);
        //Passenger doesn't exist
        if(passenger == null) {
            passenger = new Passenger();
            passenger.setEntry_lat(lat);
            passenger.setEntry_lon(longi);
            passenger.setEntry_time(time);
            //store passenger in hash map
            tmp.put(studyStartDate + ", " + studyStartTime + ", " + tag, passenger);
        } else {
            tableH.remove(studyStartDate + ", " + studyStartTime + ", " + tag);
            passenger.setExit_lat(lat);
            passenger.setExit_lon(longi);
            passenger.setExit_time(time);
            //store complete passenger info in hash map
            tmp.put(studyStartDate + ", " + studyStartTime + ", " + tag, passenger);
        }

        //send Ack to bluetooth
        sendBroadcastBT(BLUETOOTH_ACK_MESSAGE);

        //Calculate complete passenger data counter (count passengers with time_exit != null)
        int completePassengers = 0;
        List<Passenger> completePassList = new ArrayList<>();
        for(Passenger current: tmp.values()) {
            if(current.getExit_time() != null) {
                completePassengers++;
                completePassList.add(current);
            }
        }


        if(completePassengers >= BATCH_SIZE) {
            //TODO: store all locally completed passenger data from hash map in memory


            //send locally completed passengers to web app
            //Create connection, post study and receive ack
            ArcHttpClient myClient = new ArcHttpClient(this);
            myClient.post(completePassList, null, null);

        }

        //if(passenger_data_complete counter >= BATCH_SIZE) {

            //Create connection, post study and receive ack
            //ArcHttpClient myClient = new ArcHttpClient(this);
            //myClient.post(list, null, null);

            //inside while loop  -v
            //TODO: if(time data was sent - current time > THRESHOLD || ACK != OK) {send locally completed passengers to web app}
            //TODO: else if(time data was sent - current time > THRESHOLD && ACK == OK) {remove all complete passengers from hash map; break;}
        //}

        //send hash map to fragment
        sendBroadcast(tmp);
    }

    private void handleAction_DiscardStudy(String actionHTTP, String studyName, String dateCreated, String timeCreated) {
        //Creating study
        Study study = new Study(studyName, null, null, 0, dateCreated, timeCreated);

        //Create connection, post study and receive ack
        ArcHttpClient myClient = new ArcHttpClient(this);
        myClient.post(null, study, actionHTTP);

        //TODO: clear passengers of current study from memory (microSDcard)

        HashMap<String, Passenger> tmp = new HashMap<>();
        //send hash map to fragment
        sendBroadcast(tmp);
    }

    private void handleAction_CreateStudy(String actionHTTP, String studyName, String route, String type, int capacity, String dateStart, String timeStart) {
        //Creating study
        Study study = new Study(studyName, route, type, capacity, dateStart, timeStart);

        //Create connection, post study and receive ack
        ArcHttpClient myClient = new ArcHttpClient(this);
        myClient.post(null, study, actionHTTP);
    }

    private void handleAction_EditStudy(String actionHTTP, String studyName, String route, String type,
                                        int capacity, String dateCreated, String timeCreated) {

        //Creating edited study
        Study study = new Study(studyName, route, type, capacity, null, null);

        //Create connection, post study and receive ack
        ArcHttpClient myClient = new ArcHttpClient(this);
        myClient.post(null, study, actionHTTP);

        //TODO: need old values of study_name and study_dateTime_created to find which passengers in memory must change their study info (study name and date time)


        //Each passenger in memory will have: study_name, study_dateTime_created, passenger info
        //TODO: edit study name and date time of all passengers with the old study name and old date time in memory (micro SDcard)
    }

    private void handleAction_StopStudy(HashMap<String, Passenger> tableH, String actionHTTP, String studyName, String dateEnd, String timeEnd) {

        HashMap<String, Passenger> tmp = tableH;

        //TODO: calculate complete passenger data counter (count passengers with time_exit != null)
        //if (passenger_data_complete counter > 0) {
            //TODO: store all locally completed passenger data from hash map in memory
            //TODO: send locally completed passengers to web app
            //inside while loop  -v
            //TODO: if(time data was sent - current time > THRESHOLD || ACK != OK) {send locally completed passengers to web app}
            //TODO: else if(time data was sent - current time > THRESHOLD && ACK == OK) {tableH.clear(); break;}
        //}

        //Setting study finish
        Study study = new Study(studyName, null, null, 0, dateEnd, timeEnd);

        //Create connection, post study and receive ack
        ArcHttpClient myClient = new ArcHttpClient(this);
        myClient.post(null, study, actionHTTP);

        tmp.clear();
        //send hash map to fragment
        sendBroadcast(tmp);
    }

    private void handleAction_Diagnostic(HashMap<String, Passenger> tableH, String actionHTTP) {

        HashMap<String, Passenger> tmp = tableH;

        //TODO

        //send hash map to fragment
        sendBroadcast(tmp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

    private void sendBroadcast(HashMap<String, Passenger> table) {
        Intent localIntent = new Intent(StartStudyFragment.MyServiceReceiver.BROADCAST_ACTION);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);
        localIntent.putExtra(StartStudyFragment.MAP_DATA, table);
        sendBroadcast(localIntent);
    }

    private void sendBroadcastBT(String message) {
        Intent localIntent = new Intent(StartStudyFragment.MyServiceReceiver.BROADCAST_ACTION);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);
        localIntent.putExtra(StartStudyFragment.BT_ACK, message);
        sendBroadcast(localIntent);
    }
}
