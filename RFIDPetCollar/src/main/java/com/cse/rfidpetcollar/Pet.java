package com.cse.rfidpetcollar;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by Joseph on 4/7/2014.
 */
public class Pet {
    private int id;
    private String name;
    private String rfidId;
    private boolean allowed = false;
    private Date[] accessTimes;
    private static final int MAX_ACCESS_SIZE = 10;

    public Pet() { }

    public Pet (String name, String rfidId){
        this.name = name;
        this.rfidId = rfidId;
        this.accessTimes = new Date[MAX_ACCESS_SIZE];
        this.allowed = false;
    }

    public Pet (int id, String name, String rfidId) {
        this.id = id;
        this.name = name;
        this.rfidId = rfidId;
        this.accessTimes = new Date[MAX_ACCESS_SIZE];
        this.allowed = false;
    }

    public boolean getAllowed() { return this.allowed;}
    public void setAllowed(boolean allowed) { this.allowed = allowed;}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRfidId(){
        return this.rfidId;
    }

    public void setRfidId(String rfidId) {
        this.rfidId = rfidId;
    }

    public Date[] getAccessTimes() { return accessTimes; }

    public void addAccessTime(Date accessTime) {this.accessTimes[this.accessTimes.length] = accessTime; }
}
