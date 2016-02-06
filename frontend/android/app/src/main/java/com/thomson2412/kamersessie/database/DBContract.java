package com.thomson2412.kamersessie.database;

import android.provider.BaseColumns;

/**
 * Created by tfink on 16-9-2015.
 */
public final class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DBContract() {}

    /* Inner class that defines the table contents */
    public static abstract class HighscoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "highscores";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_HIGHSCORE = "highscore";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static abstract class GroupEntry implements BaseColumns {
        public static final String TABLE_NAME = "group";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_HIGHSCORE = "highscore";
        public static final String COLUMN_NAME_DATE = "date";
    }
}