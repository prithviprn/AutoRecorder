package ua.moskovkin.autorecorder.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "AutoRecorder";
    private static final int DB_VERSION = 1;
    private static final String CONTACTS_TABLE_NAME = "contacts";
    private static final String CF_ID = "id integer primary key autoincrement,";
    private static final String CF_UUID = "uuid text,";
    private static final String CF_CONTACT_NUMBER = "contact_number text,";
    private static final String CF_CONTACT_NAME = "contact_name text,";
    private static final String CF_CONTACT_IMAGE_URI = "image_uri text";
    private static final String RECORDS_TABLE_NAME = "records";
    private static final String RF_ID = "id integer primary key autoincrement,";
    private static final String RF_UUID = "uuid text,";
    private static final String RF_UUID_CONTACT = "uuid_contact text,";
    private static final String RF_RECORD_NUMBER = "record_number text,";
    private static final String RF_RECORD_FILE_NAME = "record_filename text,";
    private static final String RF_RECORD_PATH = "record_path text,";
    private static final String RF_FILE_SIZE = "file_size text,";
    private static final String RF_CONTACT_IMAGE_URI = "contact_image_uri text,";
    private static final String RF_DATE = "date text,";
    private static final String RF_IS_INCOMING = "is_incoming integer,";
    private static final String RF_DURATION_HOURS = "hours integer,";
    private static final String RF_DURATION_MINUTES = "minutes integer,";
    private static final String RF_DURATION_SECONDS = "seconds integer";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + CONTACTS_TABLE_NAME + " ("
                                                                + CF_ID
                                                                + CF_UUID
                                                                + CF_CONTACT_NUMBER
                                                                + CF_CONTACT_NAME
                                                                + CF_CONTACT_IMAGE_URI
                                                                + ");");

        db.execSQL("create table " + RECORDS_TABLE_NAME + " ("
                                                                + RF_ID
                                                                + RF_UUID
                                                                + RF_UUID_CONTACT
                                                                + RF_RECORD_NUMBER
                                                                + RF_RECORD_FILE_NAME
                                                                + RF_RECORD_PATH
                                                                + RF_FILE_SIZE
                                                                + RF_CONTACT_IMAGE_URI
                                                                + RF_DATE
                                                                + RF_IS_INCOMING
                                                                + RF_DURATION_HOURS
                                                                + RF_DURATION_MINUTES
                                                                + RF_DURATION_SECONDS
                                                                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
