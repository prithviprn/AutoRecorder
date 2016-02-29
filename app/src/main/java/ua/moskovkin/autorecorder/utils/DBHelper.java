package ua.moskovkin.autorecorder.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import ua.moskovkin.autorecorder.model.Contact;
import ua.moskovkin.autorecorder.model.Record;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "AutoRecorder";
    public static final int DB_VERSION = 1;
    public static final String TEXT_FIELD_COMA = " text,";
    public static final String TEXT_FIELD = " text";
    public static final String INTEGER_FIELD_COMA = " integer,";
    public static final String INTEGER_FIELD = " integer";
    public static final String PRIMARY_KEY =  " integer primary key autoincrement,";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CF_ID = "id";
    public static final String CF_UUID = "uuid";
    public static final String CF_CONTACT_NUMBER = "contact_number";
    public static final String CF_CONTACT_NAME = "contact_name";
    public static final String CF_CONTACT_IMAGE_URI = "image_uri";
    public static final String RECORDS_TABLE_NAME = "records";
    public static final String RF_ID = "id";
    public static final String RF_UUID = "uuid";
    public static final String RF_UUID_CONTACT = "uuid_contact";
    public static final String RF_RECORD_NUMBER = "record_number";
    public static final String RF_RECORD_FILE_NAME = "record_filename";
    public static final String RF_RECORD_PATH = "record_path";
    public static final String RF_FILE_SIZE = "file_size";
    public static final String RF_CONTACT_IMAGE_URI = "contact_image_uri";
    public static final String RF_DATE = "date";
    public static final String RF_IS_INCOMING = "is_incoming";
    public static final String RF_DURATION_HOURS = "hours";
    public static final String RF_DURATION_MINUTES = "minutes";
    public static final String RF_DURATION_SECONDS = "seconds";
    private Context context;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + CONTACTS_TABLE_NAME + " ("
                + CF_ID + PRIMARY_KEY
                + CF_UUID + TEXT_FIELD_COMA
                + CF_CONTACT_NUMBER + TEXT_FIELD_COMA
                + CF_CONTACT_NAME + TEXT_FIELD_COMA
                + CF_CONTACT_IMAGE_URI + TEXT_FIELD
                + ");");

        db.execSQL("create table " + RECORDS_TABLE_NAME + " ("
                + RF_ID + PRIMARY_KEY
                + RF_UUID + TEXT_FIELD_COMA
                + RF_UUID_CONTACT + TEXT_FIELD_COMA
                + RF_RECORD_NUMBER + TEXT_FIELD_COMA
                + RF_RECORD_FILE_NAME + TEXT_FIELD_COMA
                + RF_RECORD_PATH + TEXT_FIELD_COMA
                + RF_FILE_SIZE + TEXT_FIELD_COMA
                + RF_CONTACT_IMAGE_URI + TEXT_FIELD_COMA
                + RF_DATE + TEXT_FIELD_COMA
                + RF_IS_INCOMING + INTEGER_FIELD_COMA
                + RF_DURATION_HOURS + INTEGER_FIELD_COMA
                + RF_DURATION_MINUTES + INTEGER_FIELD_COMA
                + RF_DURATION_SECONDS + INTEGER_FIELD
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addContactNumbersAndRecordsToDb(String path) {
        File home = new File(path);
        File[] subDirs = home.listFiles();
        SQLiteDatabase db = getWritableDatabase();
        for (File file : subDirs) {
            if (file.isDirectory() && !isContactExist(file.getName())) {
                ContentValues cv = new ContentValues();
                Contact c = new Contact(
                        file.getName(),
                        Utils.getContactName(file.getName(), context),
                        Utils.getContactImage(file.getName(), context)
                );
                cv.put(DBHelper.CF_UUID, c.getId().toString());
                cv.put(DBHelper.CF_CONTACT_NUMBER, c.getContactNumber());
                cv.put(DBHelper.CF_CONTACT_NAME, c.getContactName());
                cv.put(DBHelper.CF_CONTACT_IMAGE_URI, String.valueOf(c.getContactImageUri()));

                db.insert(DBHelper.CONTACTS_TABLE_NAME, null, cv);
            } else if (isContactExist(file.getName())
                    && Utils.isContactNameInContacts(file.getName(), context)) {
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.CF_CONTACT_NAME, Utils.getContactName(file.getName(), context));
                if (Utils.isContactImageSet(file.getName(), context)) {
                    cv.put(CF_CONTACT_IMAGE_URI, Utils.getContactImage(file.getName(), context));
                }
                db.update(DBHelper.CONTACTS_TABLE_NAME, cv,
                        DBHelper.CF_CONTACT_NUMBER + " = ?", new String[]{file.getName()});
            }

            File[] records = file.listFiles();
            if (records != null && records.length > 0) {
                for (File record : records) {
                    if (record.isFile() && record.getName().contains(".")) {
                        if (!isRecordExist(record.getPath())) {
                            addRecordToDb(record);
                        } else if (isRecordExist(record.getPath())) {
                            File contactNumber = new File(record.getParent());
                            if (Utils.isContactImageSet(contactNumber.getName(), context)) {
                                insertContactImageUriToRecord(record.getPath(), Utils.getContactImage(contactNumber.getName(), context));
                            }
                        }
                    }
                }
            }
        }
    }

    private void addRecordToDb(File file) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        String[] splitedFileName  = file.getName().split("\\.");
        String contactNumber = new File(file.getParent()).getName();
        String contactUUID = getContactUUID(contactNumber);
        String fileSize = String.valueOf(file.length() / 1024);
        String fileImage = Utils.getContactImage(new File(file.getParent()).getName(), context);
        GregorianCalendar date = Utils.getCalendarFromFile(file.getPath().split("/"));
        String textDate = String.format("%02d %s, %02d:%02d:%02d",
                date.get(Calendar.DAY_OF_MONTH),
                date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE),
                date.get(Calendar.SECOND));
        int isIncoming = 0;
        if (file.getName().endsWith("I")) {
            isIncoming = 1;
        }
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(file.getPath());
        long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
        int hours = (int) (duration / 3600);
        int minutes = (int) ((duration / 60) - (hours * 60));
        int seconds = (int) (duration - (hours * 3600) - (minutes * 60));
        Record record = new Record(
                contactUUID,
                contactNumber,
                splitedFileName[0],
                file.getPath(),
                fileSize,
                fileImage,
                textDate,
                isIncoming,
                hours,
                minutes,
                seconds);

        cv.put(RF_UUID, String.valueOf(record.getId()));
        cv.put(RF_UUID_CONTACT, record.getContactId());
        cv.put(RF_RECORD_NUMBER, record.getRecordNumber());
        cv.put(RF_RECORD_FILE_NAME, record.getRecordFileName());
        cv.put(RF_RECORD_PATH, record.getRecordPath());
        cv.put(RF_FILE_SIZE, record.getFileSize());
        cv.put(RF_CONTACT_IMAGE_URI, record.getContactImageUri());
        cv.put(RF_DATE, record.getDate());
        cv.put(RF_IS_INCOMING, record.isIncoming());
        cv.put(RF_DURATION_HOURS, record.getHours());
        cv.put(RF_DURATION_MINUTES, record.getMinutes());
        cv.put(RF_DURATION_SECONDS, record.getSeconds());

        db.insert(DBHelper.RECORDS_TABLE_NAME, null, cv);
    }

    public ArrayList<Contact> getAllContacts() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + CONTACTS_TABLE_NAME, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                Contact contact = new Contact();
                contact.setId(UUID.fromString(cursor.getString(cursor.getColumnIndex(CF_UUID))));
                contact.setContactNumber(cursor.getString(cursor.getColumnIndex(CF_CONTACT_NUMBER)));
                contact.setContactName(cursor.getString(cursor.getColumnIndex(CF_CONTACT_NAME)));
                contact.setContactImageUri(cursor.getString(cursor.getColumnIndex(CF_CONTACT_IMAGE_URI)));
                contact.setRecords(getRecordForContact(String.valueOf(contact.getId())));

                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        return contacts;
    }

    private ArrayList<Record> getRecordForContact(String contactUUID) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Record> records = new ArrayList<>();
        String selection = RF_UUID_CONTACT + " =?";
        String[] selectionArgs = {contactUUID};
        Cursor corsor = db.query(
                RECORDS_TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (corsor.getCount() > 0) {
            corsor.moveToFirst();
            do {
                records.add(constructRecord(corsor));
            } while (corsor.moveToNext());
        }
        return records;
    }

    public ArrayList<Record> getAllRecords() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Record> records = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + RECORDS_TABLE_NAME, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                Record record = constructRecord(cursor);
                records.add(record);
            } while (cursor.moveToNext());
        }
        return records;
    }

    public ArrayList<Record> getIncomingRecords() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Record> records = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + RECORDS_TABLE_NAME, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                Record record = constructRecord(cursor);
                if (record.getRecordFileName().contains("I")) {
                    records.add(record);
                }
            } while (cursor.moveToNext());
        }
        return records;
    }
    public ArrayList<Record> getOutgoingRecords() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Record> records = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + RECORDS_TABLE_NAME, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                Record record = constructRecord(cursor);
                if (!record.getRecordFileName().contains("I")) {
                    records.add(record);
                }
            } while (cursor.moveToNext());
        }
        return records;
    }

    private Record constructRecord(Cursor cursor) {
        Record record = new Record();
        record.setId(UUID.fromString(cursor.getString(cursor.getColumnIndex(RF_UUID))));
        record.setContactId(cursor.getString(cursor.getColumnIndex(RF_UUID_CONTACT)));
        record.setRecordNumber(cursor.getString(cursor.getColumnIndex(RF_RECORD_NUMBER)));
        record.setRecordFileName(cursor.getString(cursor.getColumnIndex(RF_RECORD_FILE_NAME)));
        record.setRecordPath(cursor.getString(cursor.getColumnIndex(RF_RECORD_PATH)));
        record.setFileSize(cursor.getString(cursor.getColumnIndex(RF_FILE_SIZE)));
        record.setContactImageUri(cursor.getString(cursor.getColumnIndex(RF_CONTACT_IMAGE_URI)));
        record.setDate(cursor.getString(cursor.getColumnIndex(RF_DATE)));
        record.setIsIncoming(cursor.getInt(cursor.getColumnIndex(RF_IS_INCOMING)));
        record.setHours(cursor.getInt(cursor.getColumnIndex(RF_DURATION_HOURS)));
        record.setMinutes(cursor.getInt(cursor.getColumnIndex(RF_DURATION_MINUTES)));
        record.setSeconds(cursor.getInt(cursor.getColumnIndex(RF_DURATION_SECONDS)));

        return record;
    }

    public String getContactUUID(String contactNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String[] column = {CF_UUID};
        String selection = CF_CONTACT_NUMBER + " =?";
        String[] selectionArgs = {contactNumber};
        Cursor corsor = db.query(
                CONTACTS_TABLE_NAME,
                column,
                selection,
                selectionArgs,
                null,
                null,
                null);

        corsor.moveToFirst();

        return corsor.getString(0);
    }

    public void insertContactImageUriToRecord(String recordPath, String contactNameImageUri) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[] {recordPath};
        ContentValues cv = new ContentValues();
        cv.put(RF_CONTACT_IMAGE_URI, contactNameImageUri);

        db.update(RECORDS_TABLE_NAME, cv, RF_RECORD_PATH + " =?", selectionArgs);
    }

    public boolean isContactExist(String contactNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String[] column = {CF_CONTACT_NUMBER};
        String selection = CF_CONTACT_NUMBER + " =?";
        String[] selectionArgs = {contactNumber};
        Cursor corsor = db.query(
                                CONTACTS_TABLE_NAME,
                                column,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                null);

        return corsor.moveToFirst();
    }

    public boolean isRecordExist(String recordPath) {
        SQLiteDatabase db = getReadableDatabase();
        String[] column = {RF_RECORD_PATH};
        String selection = RF_RECORD_PATH + " =?";
        String[] selectionArgs = {recordPath};
        Cursor corsor = db.query(
                RECORDS_TABLE_NAME,
                column,
                selection,
                selectionArgs,
                null,
                null,
                null);

        return corsor.moveToFirst();
    }
}
