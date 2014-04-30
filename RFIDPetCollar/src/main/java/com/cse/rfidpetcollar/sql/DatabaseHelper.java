package com.cse.rfidpetcollar.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cse.rfidpetcollar.Pet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 3/5/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rfidTagManager";
    private static final String TABLE_RFID = "rfid";

    // RFID table column names
    private static final String KEY_ID = "id";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_PETNAME = "pet_name";
    private static final String KEY_PETID = "pet_id";

    public DatabaseHelper(Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RFID + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CATEGORY + " TEXT,"
                + KEY_PETNAME + " TEXT," + KEY_PETID + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading the db
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RFID);

        onCreate(db);
    }

    public void addPet(Pet pet){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        String petName = pet.getName();
        String petRfidId = pet.getRfidId();
        values.put(KEY_CATEGORY, "");
        values.put(KEY_PETNAME, petName);
        values.put(KEY_PETID, petRfidId);

        db.insert(TABLE_RFID, null, values);
        db.close();
    }

    public Pet getPet(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RFID, new String[] { KEY_ID,
        KEY_PETNAME, KEY_PETID}, KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        Pet pet = new Pet(Integer.parseInt(cursor.getString(0)),
                cursor.getString(2), cursor.getString(3));

        return pet;
    }

    public List<Pet> getAllPets(){
        List<Pet> pets = new ArrayList<Pet>();

        String selectQuery = "SELECT * FROM " + TABLE_RFID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                Pet pet = new Pet();
                pet.setId(Integer.parseInt(cursor.getString(0)));
                pet.setName(cursor.getString(2));
                pet.setRfidId(cursor.getString(3));

                pets.add(pet);
            } while (cursor.moveToNext());
        }

        return pets;
    }

    public int getPetCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RFID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int updatePet(Pet pet){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PETNAME, pet.getName());
        values.put(KEY_PETID, pet.getRfidId());

        // updating row
        return db.update(TABLE_RFID, values, KEY_ID + " = ?",
                new String[] { String.valueOf(pet.getId()) });
    }

    public void deletePet(Pet pet)  {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RFID, KEY_ID + " = ?",
                new String[] { String.valueOf(pet.getId()) });
        db.close();
    }
}
