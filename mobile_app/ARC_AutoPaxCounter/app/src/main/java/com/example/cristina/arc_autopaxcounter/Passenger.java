package com.example.cristina.arc_autopaxcounter;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cristina on 10/28/2015.
 */
public class Passenger implements Parcelable {

    private double entry_lat;
    private double entry_lon;
    private String entry_time;
    private double exit_lat;
    private double exit_lon;
    private String exit_time;

    public Passenger() {
        entry_lat = 0;
        entry_lon = 0;
        exit_lat = 0;
        exit_lon = 0;
    }

    public Passenger(double entry_lat, double entry_lon, String entry_time, double exit_lat, double exit_lon, String exit_time) {
        this.entry_lat = entry_lat;
        this.entry_lon = entry_lon;
        this.entry_time = entry_time;
        this.exit_lat = exit_lat;
        this.exit_lon = exit_lon;
        this.exit_time = exit_time;
    }

    public Passenger(Parcel source) {
        entry_lat = source.readDouble();
        entry_lon = source.readDouble();
        entry_time = source.readString();
        exit_lat = source.readDouble();
        exit_lon = source.readDouble();
        exit_time = source.readString();
    }

    public double getEntry_lat() {
        return entry_lat;
    }

    public double getEntry_lon() {
        return entry_lon;
    }

    public String getEntry_time() {
        return entry_time;
    }

    public double getExit_lat() {
        return exit_lat;
    }

    public double getExit_lon() {
        return exit_lon;
    }

    public String getExit_time() {
        return exit_time;
    }

    public void setEntry_lat(double entry_lat) {
        this.entry_lat = entry_lat;
    }

    public void setEntry_lon(double entry_lon) {
        this.entry_lon = entry_lon;
    }

    public void setEntry_time(String entry_time) {
        this.entry_time = entry_time;
    }

    public void setExit_lat(double exit_lat) {
        this.exit_lat = exit_lat;
    }

    public void setExit_lon(double exit_lon) {
        this.exit_lon = exit_lon;
    }

    public void setExit_time(String exit_time) {
        this.exit_time = exit_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(entry_lat);
        dest.writeDouble(entry_lon);
        dest.writeString(entry_time);
        dest.writeDouble(exit_lat);
        dest.writeDouble(exit_lon);
        dest.writeString(exit_time);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Passenger createFromParcel(Parcel in) {
            return new Passenger(in);
        }

        public Passenger[] newArray(int size) {
            return new Passenger[size];
        }
    };

}
