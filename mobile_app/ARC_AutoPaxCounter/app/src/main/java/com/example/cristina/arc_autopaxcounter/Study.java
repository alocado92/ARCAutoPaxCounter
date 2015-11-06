package com.example.cristina.arc_autopaxcounter;

/**
 * Created by Cristina on 10/28/2015.
 */
public class Study {

    private String name;
    private String route;
    private String type;
    private int capacity;
    private String start_date;
    private String start_time;
    private String end_date;
    private String end_time;

    public Study() {

    }

    public Study(String name, String route, String type, int capacity, String start_date, String start_time) {
        this.name = name;
        this.route = route;
        this.type = type;
        this.capacity = capacity;
        this.start_date = start_date;
        this.start_time = start_time;
    }

    public String getName() {
        return name;
    }

    public String getRoute() {
        return route;
    }

    public String getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
