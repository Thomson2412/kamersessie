package com.thomson2412.kamersessie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.thomson2412.kamersessie.database.DBContract.HighscoreEntry;

/**
 * Created by tfink on 16-9-2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMBER_TYPE = " INT";
    private static final String DATE_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + HighscoreEntry.TABLE_NAME + " (" +
                    HighscoreEntry._ID + " INTEGER PRIMARY KEY," +
                    HighscoreEntry.COLUMN_NAME_USERNAME + TEXT_TYPE + COMMA_SEP +
                    HighscoreEntry.COLUMN_NAME_HIGHSCORE + NUMBER_TYPE + COMMA_SEP +
                    HighscoreEntry.COLUMN_NAME_DATE + DATE_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HighscoreEntry.TABLE_NAME;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Highscores.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
