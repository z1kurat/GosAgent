package com.example.gosagentnewrelease.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserData extends SQLiteOpenHelper {
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    public static final String KEY_NAME_FAVORITES = "number";
    public static final String KEY_TYPE_FAVORITES = "type";
    public static final String KEY_TYPE_PARAMETERS = "INT NOT NULL";
    public static final String TABLE_NAME_FAVORITES = "favorites";

    public static final String KEY_PARAMETER = "VARCHAR(512) NOT NULL PRIMARY KEY";

    public static final String KEY_NAME_SETTINGS = "type";
    public static final String KEY_SETTINGS_LAT = "lat";
    public static final String KEY_SETTINGS_LANG = "lang";
    public static final String KEY_PARAMETER_SETTINGS = "FLOAT NOT NULL";
    public static final String TABLE_NAME_SETTINGS = "settings";

    public static final String DATABASE_NAME = "user_data";
    public static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_FAVORITES = CREATE_TABLE + TABLE_NAME_FAVORITES + " ( " + KEY_NAME_FAVORITES + " " + KEY_PARAMETER + ", " + KEY_TYPE_FAVORITES + " " + KEY_TYPE_PARAMETERS + " )";
    private static final String CREATE_TABLE_SETTINGS = CREATE_TABLE + TABLE_NAME_SETTINGS + " ( " + KEY_NAME_SETTINGS + " " + KEY_PARAMETER + ", " + KEY_SETTINGS_LAT + " " + KEY_PARAMETER_SETTINGS + ", " + KEY_SETTINGS_LANG + " " + KEY_PARAMETER_SETTINGS + " )";

    public UserData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_FAVORITES);
        database.execSQL(CREATE_TABLE_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(UserData.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}