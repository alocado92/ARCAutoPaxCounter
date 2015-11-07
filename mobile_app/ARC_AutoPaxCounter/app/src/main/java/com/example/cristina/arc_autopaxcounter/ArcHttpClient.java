package com.example.cristina.arc_autopaxcounter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Created by Cristina on 11/2/2015.
 */
public class ArcHttpClient {

    private Context context;
    private HttpURLConnection conn;
    private static final String myURL = "http://arcinnovations.ece.uprm.edu:3000/mobile";
    private static final String TAG = "MyService";

    public ArcHttpClient(Context context) {
        this.context = context;
    }

    public boolean post(List<Passenger> list, Study study, String action) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean dataReceived = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                URL url = new URL(myURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);  //get input stream
                conn.setDoOutput(true); //get output stream
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                //parsing passenger list as json
                String json;
                if(StartStudyFragment.HTTP_CREATE.equals(action)) {
                    json = serializeStartJSON(study, action);
                } else if(StartStudyFragment.HTTP_EDIT.equals(action)) {
                    json = serializeEditJSON(study, action);
                } else if(MainActivity.HTTP_DISCARD.equals(action)) {
                    json = serializeDiscardJSON(study, action);
                } else if(StartStudyFragment.HTTP_STOP.equals(action)) {
                    json = serializeStopJSON(study, action);
                } else if(StartStudyFragment.DIAGNOSTIC.equals(action)) {
                    json = serializeDiagnosticJSON(study, action);
                } else {
                    //Passenger
                    json = serializePassengerJSON(list);
                }

                //inside while loop  -v
                //TODO: if(time data was sent - current time > THRESHOLD || ACK != OK) {send locally completed passengers to web app}
                //TODO: else if(time data was sent - current time > THRESHOLD && ACK == OK) {remove all complete passengers from hash map; break;}
                //}

                boolean ackReceived = false;
                int tries = 0;
                while (!ackReceived && tries < 5) {

                    //Write to output stream
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(json);
                    out.flush();
                    out.close();
                    tries++;

                    int response = conn.getResponseCode();
                    if(response == HttpURLConnection.HTTP_OK) {
                        String result = get(conn);
                        if(result.equals("OK")) {
                            ackReceived = true;
                        }
                    } else
                        Log.d(TAG, conn.getResponseMessage());
                }

                if(tries > 5) {
                    //Could not receive ack from server
                    dataReceived = false;
                } else
                    dataReceived = true;

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
            //No internet connection
        }
        return dataReceived;
    }

    public String get(HttpURLConnection conn) {
        StringBuilder result = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader reader = new BufferedReader(in);
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            Log.d(TAG, result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String serializePassengerJSON(List<Passenger> list) {
        JSONArray array = new JSONArray();
        for(Passenger passenger : list) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("entry_lat", passenger.getEntry_lat());
                jsonObject.put("entry_log", passenger.getEntry_lon());
                jsonObject.put("entry_time", passenger.getEntry_time());
                jsonObject.put("exit_lat", passenger.getExit_lat());
                jsonObject.put("exit_log", passenger.getExit_lon());
                jsonObject.put("exit_time", passenger.getExit_time());
                array.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array.toString();
    }

    private String serializeStartJSON(Study study, String action) {
        JSONObject jsonObject = new JSONObject();
        String result = "";
        try {
            jsonObject.put("action", action);
            jsonObject.put("study", study.getName());
            jsonObject.put("route", study.getRoute());
            jsonObject.put("type", study.getType());
            jsonObject.put("capacity", study.getCapacity());
            jsonObject.put("dateTime", study.getStart_date() + ", " + study.getStart_time());
            result = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String serializeStopJSON(Study study, String action) {
        JSONObject jsonObject = new JSONObject();
        String result = "";
        try {
            jsonObject.put("action", action);
            jsonObject.put("study", study.getName());
            jsonObject.put("dateTime", study.getEnd_date() + ", " + study.getEnd_time());
            result = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String serializeEditJSON(Study study, String action) {
        JSONObject jsonObject = new JSONObject();
        String result = "";
        try {
            jsonObject.put("action", action);
            jsonObject.put("study", study.getName());
            jsonObject.put("route", study.getRoute());
            jsonObject.put("type", study.getType());
            jsonObject.put("capacity", study.getCapacity());
            result = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String serializeDiscardJSON(Study study, String action) {
        JSONObject jsonObject = new JSONObject();
        String result = "";
        try {
            jsonObject.put("action", action);
            jsonObject.put("study", study.getName());
            result = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String serializeDiagnosticJSON(Study study, String action) {
        JSONObject jsonObject = new JSONObject();
        String result = "";

        return result;
    }

}
