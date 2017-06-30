package com.weirdresonance.android.habitual;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.weirdresonance.android.habitual.HabitualActivity.HabitualContract.GuitarPractice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class HabitualActivity extends AppCompatActivity {

    /**
     * Database helper that will provide access to the database
     */
    private PracticeDbHelper mDbHelper;
    private Date timeNow;

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

    protected void onClick(View v) {
        insertPractice();
        displayDatabaseInfo();
    }

    /**
     * Insert dummy practice data into the database
     */
    private void insertPractice() {

        //

        // Get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create and object, ContentValues, with the column names as keys.
        // Random generators are called to generate all the values.
        ContentValues values = new ContentValues();
        values.put(GuitarPractice.COLUMN_DATE, dateGenerator());
        values.put(GuitarPractice.COLUMN_TIME, timeGenerator());
        values.put(GuitarPractice.COLUMN_DURATION, durationGenerator());
        values.put(GuitarPractice.COLUMN_PRACTICE_TYPE, typeGenerator());
        values.put(GuitarPractice.COLUMN_PRACTICE_RATING, ratingGenerator());

        // Insert a new row using the random values assigned above and insert them in the database
        // and return the ID of the row.
        long newRowId = db.insert(GuitarPractice.TABLE_NAME, null, values);

        // Show a toast message stating if the add was successful or not.
        if (newRowId == -1) {
            // Check to see if the row ID was -1, if it was then there was an error inserting the row.
            Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
        } else {
            // If the row ID was not -1 then the insert was successful.
            Toast.makeText(this, getString(R.string.saved_successfully) + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Temp helper method to show records that have been successfully added to the database
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define the projection by adding the columns that will be used from the table.
        String[] projection = {
                GuitarPractice._ID,
                GuitarPractice.COLUMN_DATE,
                GuitarPractice.COLUMN_TIME,
                GuitarPractice.COLUMN_DURATION,
                GuitarPractice.COLUMN_PRACTICE_TYPE,
                GuitarPractice.COLUMN_PRACTICE_RATING};

        // Query the practice table using the projection passing the table to query,
        // the columns to return (now in the projections String array, null for the columns WHERE clause,
        // null for values WHERE clause, null for grouping the rows, null for filtering the rows by groups
        // and null for the sort order.
        Cursor cursor = db.query(
                GuitarPractice.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        // Get the TextView to display the returned table data and set it to displayView.
        TextView displayView = (TextView) findViewById(R.id.dbData);

        try {
            // Add a header to the TextView by concatenating all the column headers then iterate through all
            // the rows and display them in the TextView
            displayView.setText(getString(R.string.guitar_table_contains) + " " + cursor.getCount() + " " + getString(R.string.entries) + "\n\n");
            displayView.append(GuitarPractice._ID + " - " +
                    GuitarPractice.COLUMN_DATE + " - " +
                    GuitarPractice.COLUMN_TIME + " - " +
                    GuitarPractice.COLUMN_DURATION + " - " +
                    GuitarPractice.COLUMN_TIME + " - " +
                    GuitarPractice.COLUMN_PRACTICE_RATING + "\n");

            // Get the index for each column using the cursor
            int columnIndexId = cursor.getColumnIndex(GuitarPractice._ID);
            int columnIndexDate = cursor.getColumnIndex(GuitarPractice.COLUMN_DATE);
            int columnIndexTime = cursor.getColumnIndex(GuitarPractice.COLUMN_TIME);
            int columnIndexDuration = cursor.getColumnIndex(GuitarPractice.COLUMN_DURATION);
            int columnIndexType = cursor.getColumnIndex(GuitarPractice.COLUMN_PRACTICE_TYPE);
            int columnIndexRating = cursor.getColumnIndex(GuitarPractice.COLUMN_PRACTICE_RATING);

            // Iterate through all rows in the cursor
            while (cursor.moveToNext()) {
                // Using the index get the value of the item in that location and get the String for it.
                int currentID = cursor.getInt(columnIndexId);
                String currentDate = cursor.getString(columnIndexDate);
                String currentTime = cursor.getString(columnIndexTime);
                int currentDuration = cursor.getInt(columnIndexDuration);
                String currentType = cursor.getString(columnIndexType);
                int currentRating = cursor.getInt(columnIndexRating);

                // Display all the returned values in the TextView.
                displayView.append(("\n" + currentID + " - " +
                        currentDate + " - " +
                        currentTime + " - " +
                        currentDuration + " - " +
                        currentType + " - " +
                        currentRating));
            }
        } finally {
            // Close the cursor to release resources.
            cursor.close();
        }
    }

    /**
     * Helper methods to generate random dummy data so each row is different.
     */
    // Gets the date the entry was created.
    private String dateGenerator() {
        Calendar calendar = Calendar.getInstance();
        timeNow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String date = dateFormat.format(timeNow);
        return date;
    }

    // Gets the time the entry was created.
    private String timeGenerator() {

        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        String time = dateFormat.format(timeNow);
        return time;
    }

    // Generate a random duration from 5 to 240 minutes.
    private int durationGenerator() {
        Random random = new Random();
        int duration = random.nextInt(240 - 5) + 5;
        return duration;
    }

    // Generate a random number from 1 to 5 inclusive then using a Switch statement
    // reference that value to assign a practice type.
    private String typeGenerator() {
        String type = "";
        Random random = new Random();
        int rndNum = random.nextInt(5 - 1) + 1;
        switch (rndNum) {
            case 1:
                type = getString(R.string.scales);
                break;
            case 2:
                type = getString(R.string.chords);
                break;
            case 3:
                type = getString(R.string.song);
                break;
            case 4:
                type = getString(R.string.picking);
                break;
            case 5:
                type = getString(R.string.hammer_ons_offs);
                break;
        }
        return type;
    }

    // Generate a random number from 1 to 5 to be used for the rating column.
    private int ratingGenerator() {
        Random random = new Random();
        int rating = random.nextInt(5 - 1) + 1;
        return rating;
    }

    // Empty constructor so this class can't be instantiated.
    public final class HabitualContract {

        /**
         * Inner class defining constant values for the guitar practice database table.
         */
        public final class GuitarPractice implements BaseColumns {

            /**
             * Unique ID number for the practice instance row.
             */
            public final static String _ID = BaseColumns._ID;

            /**
             * Name of the database table for guitar practice
             */
            public static final String TABLE_NAME = "practic";

            /**
             * Date of the practice.
             * <p>
             * Type: DATE
             */
            public static final String COLUMN_DATE = "date";

            /**
             * Time of the practice.
             * <p>
             * Type: TIME
             */
            public static final String COLUMN_TIME = "time";

            /**
             * Duration of the practice.
             * <p>
             * Type: Integer (minutes)
             */
            public static final String COLUMN_DURATION = "duration";

            /**
             * Type of practice (Scales, songs, picking, strumming etc).
             * <p>
             * Type: TEXT
             */
            public static final String COLUMN_PRACTICE_TYPE = "type";

            /**
             * Rating for the practice. How did it go?
             * <p>
             * Type: INTEGER
             */
            public static final String COLUMN_PRACTICE_RATING = "rating";
        }
    }

    /**
     * DB Helper class
     */
    public class PracticeDbHelper extends SQLiteOpenHelper {

        /**
         * Name for the new database file
         */
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
         *
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Concatenate the contract static Strings and DB parameters into the SQL statement that will create the practice table.
            String SQL_CREATE_PRACTICE_TABLE = "CREATE TABLE " + GuitarPractice.TABLE_NAME + " ("
                    + GuitarPractice._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GuitarPractice.COLUMN_DATE + " TEXT NOT NULL, "
                    + GuitarPractice.COLUMN_TIME + " TEXT NOT NULL, "
                    + GuitarPractice.COLUMN_DURATION + " INTEGER NOT NULL, "
                    + GuitarPractice.COLUMN_PRACTICE_TYPE + " TEXT NOT NULL, "
                    + GuitarPractice.COLUMN_PRACTICE_RATING + " INTEGER NOT NULL DEFAULT 0);";

            // Execute the SQL statement.
            db.execSQL(SQL_CREATE_PRACTICE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // The database is still at version 1, so there's nothing to do be done here.
        }
    }
}
