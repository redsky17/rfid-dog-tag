package com.cse.rfidpetcollar;

/**
 * Created by Joseph on 3/5/14.
 */
public class RfidEntry {
    int id;
    String category;
    String petName;

    // constructors

    public RfidEntry(){

    }

    public RfidEntry(int id, String category, String petName){
        this.id = id;
        this.category = category;
        this.petName = petName;
    }

    public RfidEntry(String category, String petName){
        this.category = category;
        this.petName = petName;
    }

    // getters and setters
    public int getId(){
        return this.id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String value) {
        this.category = value;
    }

    public String getPetName() {
        return this.petName;
    }

    public void setPetName(String value) {
        this.petName = value;
    }

}
