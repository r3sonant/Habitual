package com.weirdresonance.android.habitual;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.weirdresonance.android.habitual.HabitualActivity.HabitualContract;
import com.weirdresonance.android.habitual.HabitualActivity.HabitualContract.GuitarPractice;

import java.security.PublicKey;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HabitualActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private PracticeDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitual);

        mDbHelper = new PracticeDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        insertPractice();
        displayDatabaseInfo();
    }

    // Empty constructor so this class can't be instantiated.
    public final class HabitualContract {

        /**
         * Inner class defining constant values for the guitar practice database table.
         */
        public final class GuitarPractice implements BaseColumns {

            /** Unique ID number for the practice instance row. */
            public final static String _ID = BaseColumns._ID;

            /** Name of the database table for guitar practice */
            public static final String TABLE_NAME = "practic";

            /** Date of the practice.
             *
             * Type: DATE
             */
            public static final String COLUMN_DATE= "date";

            /**
             * Time of the practice.
             *
             * Type: TIME
             */
            public static final String COLUMN_TIME = "time";

            /**
             * Duration of the practice.
             *
             * Type: Integer (minutes)
             */
            public static final String COLUMN_DURATION = "duration";

            /**
             * Type of practice (Scales, songs, picking, strumming etc).
             *
             * Type: TEXT
             */
            public static final String COLUMN_PRACTICE_TYPE = "type";

            /**
             * Rating for the practice. How did it go?
             *
             * Type: INTEGER
             */
            public static final String COLUMN_PRACTICE_RATING = "rating";

            /**
             * Possible values for practice rating.
             */
            public static final int RATING_ZERO = 0;
            public static final int RATING_ONE = 1;
            public static final int RATING_TWO = 2;
            public static final int RATING_THREE = 3;
            public static final int RATING_FOUR = 4;
            public static final int RATING_FIVE = 5;
        }
    }

    public class PracticeDbHelper extends SQLiteOpenHelper {

        public final String LOG_TAG = PracticeDbHelper.class.getSimpleName();

        /** Name for the new database file */
        private static final String DATABASE_NAME = "practice.db";

        /**
         * Version of database. This needs to be incremented if the database schema is changed.
         */
        private static final int DATABASE_VERSION = 1;

        public PracticeDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * This will be called when the database is initially created.
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Concatenate the contract static Strings and DB parameters into the SQL statement that will create the practice table.
            String SQL_CREATE_PRACTICE_TABLE = "CREATE TABLE "  + GuitarPractice.TABLE_NAME + " ("
                    + GuitarPractice._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GuitarPractice.COLUMN_DATE + " TEXT NOT NULL, "
                    + GuitarPractice.COLUMN_TIME + " TEXT NOT NULL, "
                    + GuitarPractice.COLUMN_DURATION + " INTEGER NOT NULL, "
                    + GuitarPractice.COLUMN_PRACTICE_TYPE + " TEXT NOT NULL, "
                    + GuitarPractice.COLUMN_PRACTICE_RATING + " INTEGER NOT NULL DEFAULT 0);";

            // Now execute the SQL statement.
            db.execSQL(SQL_CREATE_PRACTICE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // The database is still at version 1, so there's nothing to do be done here.
        }
    }

    /**
     * Insert dummy practice data into the database
     */
    private void insertPractice() {

        //

        // Get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(GuitarPractice.COLUMN_DATE, dateGenerator());
        values.put(GuitarPractice.COLUMN_TIME, "12:30:26");
        values.put(GuitarPractice.COLUMN_DURATION, 60);
        values.put(GuitarPractice.COLUMN_PRACTICE_TYPE, "Scales");
        values.put(GuitarPractice.COLUMN_PRACTICE_RATING, 5);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert(GuitarPractice.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GuitarPractice._ID,
                GuitarPractice.COLUMN_DATE,
                GuitarPractice.COLUMN_TIME,
                GuitarPractice.COLUMN_DURATION,
                GuitarPractice.COLUMN_PRACTICE_TYPE,
                GuitarPractice.COLUMN_PRACTICE_RATING };

        // Perform a query on the pets table
        Cursor cursor = db.query(
                GuitarPractice.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = (TextView) findViewById(R.id.dbData);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The Guitar Practice Table Contains the following entries " + cursor.getCount() + "\n\n");
            displayView.append(GuitarPractice._ID + " - " +
                    GuitarPractice.COLUMN_DATE + " - " +
                    GuitarPractice.COLUMN_TIME + " - " +
                    GuitarPractice.COLUMN_DURATION + " - " +
                    GuitarPractice.COLUMN_TIME + " - " +
                    GuitarPractice.COLUMN_PRACTICE_RATING + "\n");

            // Figure out the index of each column
            int columnIndexId = cursor.getColumnIndex(GuitarPractice._ID);
            int columnIndexDate = cursor.getColumnIndex(GuitarPractice.COLUMN_DATE);
            int columnIndexTime = cursor.getColumnIndex(GuitarPractice.COLUMN_TIME);
            int columnIndexDuration = cursor.getColumnIndex(GuitarPractice.COLUMN_DURATION);
            int columnIndexType = cursor.getColumnIndex(GuitarPractice.COLUMN_PRACTICE_TYPE);
            int columnIndexRating = cursor.getColumnIndex(GuitarPractice.COLUMN_PRACTICE_RATING);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(columnIndexId);
                String currentDate = cursor.getString(columnIndexDate);
                String currentTime = cursor.getString(columnIndexTime);
                int currentDuration = cursor.getInt(columnIndexDuration);
                String currentType = cursor.getString(columnIndexType);
                int currentRating = cursor.getInt(columnIndexRating);


                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentDate + " - " +
                        currentTime + " - " +
                        currentDuration + " - " +
                        currentType + " - " +
                        currentRating));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private String dateGenerator() {
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
        String date = df.format(cal.getTime());
/*        BreakIterator txtDateView = null;
        txtDateView.setText(date_str);*/
        return date;
    }

    private String timeGenerator(String time) {
        return time;
    }

    private int durationGenerator(int duration) {
        return duration;
    }

    private String typeGenerator(String type) {
        return type;
    }

    private int ratingGenerator(int rating) {
        return rating;
    }
}
