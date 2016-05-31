/*
 * Copyright 2015, Tanmay Parikh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.calebjones.spacelaunchnow.content.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.calebjones.spacelaunchnow.content.models.legacy.RocketDetails;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "SpaceLaunchNow.db";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase DB;

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_REAL = " REAL";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String COMMA_SEP = ",";

    private static final String TABLE_POST = "LaunchVehicles";
    private static final String LVName = "LVName";
    private static final String LVFamily = "LVFamily";
    private static final String LVSFamily = "LVSFamily";
    private static final String LVManufacturer = "LVManufacturer";
    private static final String LVVariant = "LVVariant";
    private static final String LVAlias = "LVAlias";
    private static final String MinStage = "MinStage";
    private static final String MaxStage = "MaxStage";
    private static final String Length = "Length";
    private static final String Diameter = "Diameter";
    private static final String LaunchMass = "LaunchMass";
    private static final String LEOCapacity = "LEOCapacity";
    private static final String GTOCapacity = "GTOCapacity";
    private static final String TOThrust = "TOThrust";
    private static final String Class = "Class";
    private static final String Apogee = "Apogee";
    private static final String ImageURL = "ImageURL";
    private static final String InfoURL = "InfoURL";
    private static final String WikiURL = "WikiURL";
    private static final String Description = "Description";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_POST + "(" +
                    "id integer primary key autoincrement" + COMMA_SEP +
                    LVName + TYPE_TEXT + COMMA_SEP +
                    LVFamily + TYPE_TEXT + COMMA_SEP +
                    LVSFamily + TYPE_TEXT + COMMA_SEP +
                    LVManufacturer + TYPE_TEXT + COMMA_SEP +
                    LVVariant + TYPE_TEXT + COMMA_SEP +
                    LVAlias + TYPE_TEXT + COMMA_SEP +
                    MinStage + TYPE_INTEGER + COMMA_SEP +
                    MaxStage + TYPE_INTEGER + COMMA_SEP +
                    Length + TYPE_TEXT + COMMA_SEP +
                    Diameter + TYPE_TEXT + COMMA_SEP +
                    LaunchMass + TYPE_TEXT + COMMA_SEP +
                    LEOCapacity + TYPE_TEXT + COMMA_SEP +
                    GTOCapacity + TYPE_TEXT + COMMA_SEP +
                    TOThrust + TYPE_TEXT + COMMA_SEP +
                    Class + TYPE_TEXT + COMMA_SEP +
                    Apogee + TYPE_TEXT + COMMA_SEP +
                    Description + TYPE_TEXT + COMMA_SEP +
                    ImageURL + TYPE_TEXT + COMMA_SEP +
                    InfoURL + TYPE_TEXT + COMMA_SEP +
                    WikiURL + TYPE_TEXT + ")";

    private static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_POST;

    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }

    public void rebuildDB(SQLiteDatabase db){
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }

    public void addPost(RocketDetails item) {
        if (feedExists(item)) {
            return;
        } else if (itemExists(item)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(LVName, item.getLVName() + " " + item.getLVVariant());
        values.put(LVFamily, item.getLVFamily());
        values.put(LVSFamily, item.getLVSFamily());
        values.put(LVManufacturer, item.getLVManufacturer());
        values.put(LVVariant, item.getLVVariant());
        values.put(LVAlias, item.getLVAlias());
        values.put(MinStage, item.getMinStage());
        values.put(MaxStage, item.getMaxStage());
        values.put(Length, item.getLength());
        values.put(Diameter, item.getDiameter());
        values.put(LaunchMass, item.getLaunchMass());
        values.put(LEOCapacity, item.getLEOCapacity());
        values.put(GTOCapacity, item.getGTOCapacity());
        values.put(TOThrust, item.getTOThrust());
        values.put(Class, item.getClass_());
        values.put(Apogee, item.getApogee());
        values.put(ImageURL, item.getImageURL());
        values.put(Description, item.getDescription());
        values.put(InfoURL, item.getInfoURL());
        values.put(WikiURL, item.getWikiURL());

        getWritableDatabase().insert(TABLE_POST, null, values);
    }


    public RocketDetails getLaunchVehicle(String LaunchVehicleName) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_POST + " WHERE " + LVName + " LIKE '%" + LaunchVehicleName +"%'", null);
        Timber.d("DatabaseManager - Checking for Vehicle: %s - Cursor Size %s:", LaunchVehicleName, cursor.getCount());
        Timber.v("DatabaseManager - Cursor: %s", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            RocketDetails item = new RocketDetails();
            item.setLVName(cursor.getString(1));
            item.setLVFamily(cursor.getString(2));
            item.setLVSFamily(cursor.getString(3));
            item.setLVManufacturer(cursor.getString(4));
            item.setLVVariant(cursor.getString(5));
            item.setLVAlias(cursor.getString(6));
            item.setMinStage(cursor.getInt(7));
            item.setMaxStage(cursor.getInt(8));
            item.setLength(cursor.getString(9));
            item.setDiameter(cursor.getString(10));
            item.setLaunchMass(cursor.getString(11));
            item.setLEOCapacity(cursor.getString(12));
            item.setGTOCapacity(cursor.getString(13));
            item.setTOThrust(cursor.getString(14));
            item.setClass_(cursor.getString(15));
            item.setApogee(cursor.getString(16));
            item.setDescription(cursor.getString(17));
            item.setImageURL(cursor.getString(18));
            item.setInfoURL(cursor.getString(19));
            item.setWikiURL(cursor.getString(20));
            cursor.close();
            return item;
        }
        return null;
    }

    public List<RocketDetails> getAllLaunchVehicle() {
        List<RocketDetails> vehicles = Collections.emptyList();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_POST, null);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            vehicles = new ArrayList<>();
            do {
                RocketDetails item = new RocketDetails();
                item.setLVName(cursor.getString(1));
                item.setLVFamily(cursor.getString(2));
                item.setLVSFamily(cursor.getString(3));
                item.setLVManufacturer(cursor.getString(4));
                item.setLVVariant(cursor.getString(5));
                item.setLVAlias(cursor.getString(6));
                item.setMinStage(cursor.getInt(7));
                item.setMaxStage(cursor.getInt(8));
                item.setLength(cursor.getString(9));
                item.setDiameter(cursor.getString(10));
                item.setLaunchMass(cursor.getString(11));
                item.setLEOCapacity(cursor.getString(12));
                item.setGTOCapacity(cursor.getString(13));
                item.setTOThrust(cursor.getString(14));
                item.setClass_(cursor.getString(15));
                item.setApogee(cursor.getString(16));
                item.setDescription(cursor.getString(17));
                item.setImageURL(cursor.getString(18));
                item.setInfoURL(cursor.getString(19));
                item.setWikiURL(cursor.getString(20));
                vehicles.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return vehicles;
    }

    public List<RocketDetails> search(String keyWord) {
        List<RocketDetails> post = Collections.emptyList();
        int num = 0;
        if (Utils.isNumeric(keyWord)) {
            num = Integer.parseInt(keyWord);
        }
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_POST + " WHERE " + LVName +
                " LIKE '%" + keyWord + "%' " + " ORDER BY date(date) DESC" , null);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            post = new ArrayList<>();
            do {
                RocketDetails item = new RocketDetails();
                item.setLVName(cursor.getString(1));
                item.setLVFamily(cursor.getString(2));
                item.setLVSFamily(cursor.getString(3));
                item.setLVManufacturer(cursor.getString(4));
                item.setLVVariant(cursor.getString(5));
                item.setLVAlias(cursor.getString(6));
                item.setMinStage(cursor.getInt(7));
                item.setMaxStage(cursor.getInt(8));
                item.setLength(cursor.getString(9));
                item.setDiameter(cursor.getString(10));
                item.setLaunchMass(cursor.getString(11));
                item.setLEOCapacity(cursor.getString(12));
                item.setGTOCapacity(cursor.getString(13));
                item.setTOThrust(cursor.getString(14));
                item.setClass_(cursor.getString(15));
                item.setApogee(cursor.getString(16));
                item.setDescription(cursor.getString(17));
                item.setImageURL(cursor.getString(18));
                item.setInfoURL(cursor.getString(19));
                item.setWikiURL(cursor.getString(20));
                post.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return post;
    }

    public int getCount() {
        int count = 0;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_POST, null);
        if (cursor != null && cursor.getCount() != 0) {
            count = cursor.getCount();
            cursor.close();
        } else if (cursor != null){
            cursor.close();
        }
        return count;
    }


    public boolean feedExists(RocketDetails item) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_POST
                + " WHERE " + LVName + " = ?", new String[]{String.valueOf(item.getLVName())});

        boolean exists = false;
        if (cursor != null && cursor.getCount() != 0) {
            exists = true;
            Timber.d("DatabaseManager - Item %s %s Exists: %s", item.getLVName(), item.getLVVariant(), true);
        }
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    public boolean itemExists(RocketDetails item) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_POST
                + " WHERE " + LVName + " = ?", new String[]{String.valueOf(item.getLVName())});
        boolean exists = false;
        if (cursor != null && cursor.getCount() != 0) {
            exists = true;
        }
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }
}
