package com.example.cristina.arc_autopaxcounter;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private static final String VERIFY_ROUTE = "Verify route validity";

    public static final String ARC_SDCARD_DEFAULT_FILENAME = "PassengerData.txt";
    public static final String BLUETOOTH_ACK_MESSAGE = "A";
    public static final String BLUETOOTH_ACK_MESSAGE_DIAGNOSTIC = "D";
    public static final String BLUETOOTH_ACK_MESSAGE_STOP = "S";
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
        intent.putExtra(StartStudyFragment.HTTP_DISCARD, action);
        intent.putExtra("studyName", studyName);
        intent.putExtra("dateCreated", dateCreated);
        intent.putExtra("timeCreated", timeCreated);
        context.startService(intent);
    }

    public static void prepareVerifyStudy(Context context, String action, String route) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(VERIFY_ROUTE);
        intent.putExtra(StartStudyFragment.HTTP_VERIFY, action);
        intent.putExtra("studyRoute", route);
        context.startService(intent);
    }

    public static void prepareEditStudy(Context context, String action, String studyName, String route, String type, int capacity) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(EDIT_STUDY);
        intent.putExtra(StartStudyFragment.HTTP_EDIT, action);
        intent.putExtra("studyName", studyName);
        intent.putExtra("route", route);
        intent.putExtra("type", type);
        intent.putExtra("capacity", capacity);
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

    public static void prepareStopStudy(Context context, String action, HashMap<String, Passenger> tableH, String name, String dateStart, String timeStart,
                                        String dateEnd, String timeEnd, boolean isFirstWriteToSDcard, String fileName) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(STOP_STUDY);
        intent.putExtra(StartStudyFragment.HTTP_STOP, action);
        intent.putExtra("table", tableH);
        intent.putExtra("studyName", name);
        intent.putExtra("dateStart", dateStart);
        intent.putExtra("timeStart", timeStart);
        intent.putExtra("dateEnd", dateEnd);
        intent.putExtra("timeEnd", timeEnd);
        intent.putExtra("fileName", fileName);
        intent.putExtra("isFirstWrite", isFirstWriteToSDcard);
        context.startService(intent);
    }

    public static void preparePassengerInfo(Context context, HashMap<String, Passenger> tableH, double lat, double longi, String time, String tag, String studyName,
                                            String studyStartDate, String studyStartTime, boolean isFirstWriteToSDcard, String fileName) {
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
        intent.putExtra("fileName", fileName);
        intent.putExtra("isFirstWrite", isFirstWriteToSDcard);
        context.startService(intent);
    }

    public static void prepareDiagnosticProtocol(Context context, String action, double lat, double longi, String time,
                                                 String tag, String studyName, String studyStartDate, String studyStartTime, boolean isDiagnostic, String fileName) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(RUN_DIAGNOSTIC);
        intent.putExtra(StartStudyFragment.DIAGNOSTIC, action);
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", longi);
        intent.putExtra("time", time);
        intent.putExtra("tag", tag);
        intent.putExtra("studyName", studyName);
        intent.putExtra("studyStartDate", studyStartDate);
        intent.putExtra("studyStartTime", studyStartTime);
        intent.putExtra("fileName", fileName);
        intent.putExtra("isDiagnostic", isDiagnostic);
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
                String fileName = bundle.getString("fileName");
                boolean isFirstWriteToSDcard = bundle.getBoolean("isFirstWrite");
                handleAction_PassengerInfo(tableH, lat, longi, time, tag, studyName, studyStartDate, studyStartTime, isFirstWriteToSDcard, fileName);
            } else if (DISCARD_STUDY.equals(action)) {
                String actionHTTP = bundle.getString(StartStudyFragment.HTTP_DISCARD);
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
                handleAction_EditStudy(actionHTTP, studyName, route, type, capacity);
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
                String dateStart = bundle.getString("dateStart");
                String timeStart = bundle.getString("timeStart");
                String dateEnd = bundle.getString("dateEnd");
                String timeEnd = bundle.getString("timeEnd");
                String fileName = bundle.getString("fileName");
                boolean isFirstWriteToSDcard = bundle.getBoolean("isFirstWrite");
                handleAction_StopStudy(tableH, actionHTTP, studyName, dateStart, timeStart, dateEnd, timeEnd, isFirstWriteToSDcard, fileName);
            } else if (RUN_DIAGNOSTIC.equals(action)) {
                String actionHTTP = bundle.getString(StartStudyFragment.DIAGNOSTIC);
                double lat = bundle.getDouble("latitude");
                double longi = bundle.getDouble("longitude");
                String time = bundle.getString("time");
                String tag = bundle.getString("tag");
                String studyName = bundle.getString("studyName");
                String studyStartDate = bundle.getString("studyStartDate");
                String studyStartTime = bundle.getString("studyStartTime");
                String fileName = bundle.getString("fileName");
                boolean isDiagnostic = bundle.getBoolean("isDiagnostic");
                handleAction_Diagnostic(actionHTTP, lat, longi, time, tag, studyName, studyStartDate, studyStartTime, isDiagnostic, fileName);
            } else if (VERIFY_ROUTE.equals(action)) {
                String actionHTTP = bundle.getString(StartStudyFragment.HTTP_VERIFY);
                String route = bundle.getString("studyRoute");
                handleAction_Verification(actionHTTP, route);
            }
        }
    }

    private void handleAction_Verification(String actionHTTP, String route) {
        Study study = new Study();
        study.setRoute(route);

        ArcHttpClient myClient = new ArcHttpClient(this);
        boolean isDataReceived = myClient.post(null, study, actionHTTP);
    }

    private void handleAction_PassengerInfo(HashMap<String, Passenger> tableH, double lat, double longi, String time, String tag, String studyName, String studyStartDate,
                                            String studyStartTime, boolean isFirstWriteToSDcard, String fileName) {

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
        HashMap<String, Passenger> completePassList = new HashMap<>();
        for(String key: tmp.keySet()) {
            Passenger currentPass = tmp.get(key);
            if(currentPass.getExit_time() != null) {
                completePassengers++;
                String a[] = key.split(",");
                String passKey = a[2].trim();
                completePassList.put(passKey, currentPass);
            }
        }

        if(completePassengers >= BATCH_SIZE) {
            //Create study with name and start datetime created to send in passenger information
            Study study = new Study(studyName, null, null, 0, studyStartDate, studyStartTime);

            //send locally completed passengers to web app
            //Create connection, post study and receive ack
            ArcHttpClient myClient = new ArcHttpClient(this);
            boolean isDataReceived = myClient.post(completePassList, study, null);

            if(isDataReceived) {
                //store all locally completed passenger data from hash map in memory
                if(this.isExternalStorageWritable()) {
                    isFirstWriteToSDcard = this.storePassengers(completePassList, studyName, studyStartDate + " " + studyStartTime, isFirstWriteToSDcard, false, fileName);
                }

                //remove all completed passengers from hash map
                HashMap<String, Passenger> tmp1 = (HashMap<String, Passenger>) tmp.clone();

                for (String key : tmp1.keySet()) {
                    Passenger current = tmp1.get(key);
                    if (tmp.containsKey(key) && current.getExit_time() != null) {
                        tmp.remove(key);
                    }
                }
            }
        }

        //send hash map to fragment
        sendBroadcast(tmp, isFirstWriteToSDcard, false);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * returns a list of all available sd cards paths, or null if not found.
     *
     * @param includePrimaryExternalStorage set to true if you wish to also include the path of the primary external storage
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static List<String> getSdCardPaths(final Context context,final boolean includePrimaryExternalStorage)
    {
        final File[] externalCacheDirs= ContextCompat.getExternalCacheDirs(context);
        if(externalCacheDirs==null||externalCacheDirs.length==0)
            return null;
        if(externalCacheDirs.length==1)
        {
            if(externalCacheDirs[0]==null)
                return null;
            final String storageState= EnvironmentCompat.getStorageState(externalCacheDirs[0]);
            if(!Environment.MEDIA_MOUNTED.equals(storageState))
                return null;
            if(!includePrimaryExternalStorage&& Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB&&Environment.isExternalStorageEmulated())
                return null;
        }
        final List<String> result=new ArrayList<>();
        if(includePrimaryExternalStorage||externalCacheDirs.length==1)
            result.add(getRootOfInnerSdCardFolder(externalCacheDirs[0]));
        for(int i=1;i<externalCacheDirs.length;++i)
        {
            final File file=externalCacheDirs[i];
            if(file==null)
                continue;
            final String storageState=EnvironmentCompat.getStorageState(file);
            if(Environment.MEDIA_MOUNTED.equals(storageState))
                result.add(getRootOfInnerSdCardFolder(externalCacheDirs[i]));
        }
        if(result.isEmpty())
            return null;
        return result;
    }

    /** Given any file/folder inside an sd card, this will return the path of the sd card */
    private static String getRootOfInnerSdCardFolder(File file)
    {
        if(file==null)
            return null;
        final long totalSpace=file.getTotalSpace();
        while(true)
        {
            final File parentFile=file.getParentFile();
            if(parentFile==null||parentFile.getTotalSpace()!=totalSpace)
                return file.getAbsolutePath();
            file=parentFile;
        }
    }

    private boolean storePassengers(HashMap<String, Passenger> passengersList, String studyName, String startDateTime, boolean isFirstWriteToSDcard, boolean isDiagnostic,
                                    String fileName) {
        /*Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
        File externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
        File dataFile = new File(externalSdCard, fileName);*/
        List getSDpath = getSdCardPaths(this, false);
        String externalSdCard = (String) getSDpath.get(0);
        File dataFile = new File(externalSdCard, fileName);

        try{
            //if file doesn't exists, then create it
            if(!dataFile.exists()){
                dataFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(dataFile, true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            if(isFirstWriteToSDcard) {
                bufferWriter.write("=======Study: " +studyName + " === DateTime: " + startDateTime + "=======\n\n");
                isFirstWriteToSDcard = false;
            }

            //Each passenger in memory will have: study_name, study_dateTime_created, passenger info
            if(!isDiagnostic) {
                for (String key : passengersList.keySet()) {
                    Passenger current = passengersList.get(key);
                    bufferWriter.write("Study: [" + studyName + "], Start_datetime: [" + startDateTime + "], TagID: [" + key + "], Passenger: [" + current.toString() + "]\n");
                }

                bufferWriter.write("---------\n");
                bufferWriter.close();
                fileWriter.close();
                Log.d(ArcHttpClient.TAG, "Done writing passengers in SDcard");
            } else {
                Intent localIntent = new Intent(StartStudyFragment.MyServiceReceiver.BROADCAST_ACTION);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.putExtra(ArcHttpClient.TOAST_MSG, "Simulation of data storage in sdcard complete");
                localIntent.putExtra(StartStudyFragment.MAP_FLAG, false);
                sendBroadcast(localIntent);
            }

            //Read from file
            /*FileReader fileReader = new FileReader(dataFile);
            BufferedReader bufferReader = new BufferedReader(fileReader);

            StringBuilder sb = new StringBuilder();
            String line = bufferReader.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = bufferReader.readLine();
            }
            String everything = sb.toString();
            Log.d(ArcHttpClient.TAG, "Done reading passengers from SDcard");*/

        }catch(IOException e){
            Log.d(ArcHttpClient.TAG, "Cannot write in sdcard", e);
            e.printStackTrace();
        }

        return isFirstWriteToSDcard;
    }

    private void handleAction_DiscardStudy(String actionHTTP, String studyName, String dateCreated, String timeCreated) {
        //Creating study
        Study study = new Study(studyName, null, null, 0, dateCreated, timeCreated);

        //Create connection, post study and receive ack
        ArcHttpClient myClient = new ArcHttpClient(this);
        myClient.post(null, study, actionHTTP);

        HashMap<String, Passenger> tmp = new HashMap<>();
        //send hash map to fragment
        sendBroadcast(tmp, false, true);
    }

    private void handleAction_CreateStudy(String actionHTTP, String studyName, String route, String type, int capacity, String dateStart, String timeStart) {
        //Creating study
        Study study = new Study(studyName, route, type, capacity, dateStart, timeStart);

        //Create connection, post study and receive ack
        ArcHttpClient myClient = new ArcHttpClient(this);
        myClient.post(null, study, actionHTTP);
    }

    private void handleAction_EditStudy(String actionHTTP, String studyName, String route, String type,
                                        int capacity) {

        //Creating edited study
        Study study = new Study(studyName, route, type, capacity, null, null);

        //Create connection, post study and receive ack
        ArcHttpClient myClient = new ArcHttpClient(this);
        myClient.post(null, study, actionHTTP);
    }

    private void handleAction_StopStudy(HashMap<String, Passenger> tableH, String actionHTTP, String studyName, String dateStart, String timeStart,
                                        String dateEnd, String timeEnd, boolean isFirstWriteToSDcard, String fileName) {

        HashMap<String, Passenger> tmp = tableH;

        //Setting study finish
        Study study = new Study(studyName, dateStart, timeStart, 0, dateEnd, timeEnd);

        //Calculate complete passenger data counter (count passengers with time_exit != null)
        HashMap<String, Passenger> completePassList = new HashMap<>();
        for(String key: tmp.keySet()) {
            Passenger currentPass = tmp.get(key);
            if(currentPass.getExit_time() != null) {
                String a[] = key.split(",");
                String passKey = a[2].trim();
                completePassList.put(passKey, currentPass);
            }
        }

        ArcHttpClient myClient = new ArcHttpClient(this);
        if(completePassList.size() > 0) {
            //store all locally completed passenger data from hash map in memory
            if (this.isExternalStorageWritable()) {
                isFirstWriteToSDcard = this.storePassengers(completePassList, studyName, dateEnd + " " + timeEnd, isFirstWriteToSDcard, false, fileName);
            }

            //send locally completed passengers to web app
            //Create connection, post study and receive ack
            myClient.post(completePassList, study, null);

            tmp.clear();
        }

        Date time = Calendar.getInstance().getTime();
        long timeStop = time.getTime();
        boolean stop = false;

        while(!stop) {
            time = Calendar.getInstance().getTime();
            long diff = time.getTime() - timeStop;
            if(diff > 2000) {
                //Create connection, post study and receive ack
                myClient.post(null, study, actionHTTP);
                stop = true;
            }
        }

        //send hash map to fragment
        sendBroadcast(tmp, isFirstWriteToSDcard, true);

        //send Ack to bluetooth
        sendBroadcastBT(BLUETOOTH_ACK_MESSAGE_STOP);
    }

    private void handleAction_Diagnostic(String actionHTTP, double lat, double longi, String time, String tag,
                                         String studyName, String studyStartDate, String studyStartTime, boolean isDiagnostic, String fileName) {

        HashMap<String, Passenger> tmp = new HashMap<>();
        Passenger passenger = new Passenger(lat, longi, time, 0, 0, null);
        tmp.put(studyStartDate + ", " + studyStartTime + ", " + tag, passenger);

        //store partially completed passenger data from hash map in memory
        if(this.isExternalStorageWritable()) {
            this.storePassengers(tmp, studyName, studyStartDate + " " + studyStartTime, false, isDiagnostic, fileName);
        }

        //send Ack to bluetooth
        sendBroadcastBT(BLUETOOTH_ACK_MESSAGE);

        //send passengers to web app
        //Create connection, post study and receive ack
        ArcHttpClient myClient = new ArcHttpClient(this);
        boolean isDataReceived = myClient.post(tmp, null, actionHTTP);

        if(isDataReceived) {
            //send Ack to bluetooth
            sendBroadcastBT(BLUETOOTH_ACK_MESSAGE_DIAGNOSTIC);
        }

        tmp.clear();

        //send Ack to bluetooth
        sendBroadcastBT(BLUETOOTH_ACK_MESSAGE_STOP);
    }

    private void sendBroadcast(HashMap<String, Passenger> table, boolean isFirstWriteToSDcard, boolean isStop) {
        Intent localIntent = new Intent(StartStudyFragment.MyServiceReceiver.BROADCAST_ACTION);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);
        localIntent.putExtra(StartStudyFragment.MAP_DATA, table);
        localIntent.putExtra(StartStudyFragment.STUDY_FIRST_WRITE, isFirstWriteToSDcard);
        localIntent.putExtra(StartStudyFragment.STOP_STUDY, isStop);
        localIntent.putExtra(StartStudyFragment.MAP_FLAG, true);
        sendBroadcast(localIntent);
    }

    public void sendBroadcastBT(String message) {
        Intent localIntent = new Intent(ARC_Bluetooth.BROADCAST_ACTION_ACK);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);
        localIntent.putExtra(ARC_Bluetooth.BT_ACK, message);
        localIntent.putExtra(StartStudyFragment.MAP_FLAG, false);
        sendBroadcast(localIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}