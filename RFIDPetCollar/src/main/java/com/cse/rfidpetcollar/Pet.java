package com.cse.rfidpetcollar;

/**
 * Created by Joseph on 4/7/2014.
 */
public class Pet {
    private int id;
    private String name;
    private String rfidId;

    public Pet() { }

    public Pet (String name, String rfidId){
        this.name = name;
        this.rfidId = rfidId;
    }

    public Pet (int id, String name, String rfidId) {
        this.id = id;
        this.name = name;
        this.rfidId = rfidId;
    }

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
}
