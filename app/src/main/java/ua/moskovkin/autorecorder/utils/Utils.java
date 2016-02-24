package ua.moskovkin.autorecorder.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class Utils {

    public static String getContactName(String phoneNumber, Context context) {
        Uri uri = Uri.parse("content://com.android.contacts/phone_lookup");
        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME};

        uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        String contactName = "";

        if (cursor.moveToFirst())
        {
            contactName = cursor.getString(0);
        }
        cursor.close();

        return contactName;
    }

    public static String getContactImage(String phoneNumber, Context context) {
        Uri uri = Uri.parse("content://com.android.contacts/phone_lookup");
        String[] projection = new String[] {ContactsContract.PhoneLookup.PHOTO_URI};

        uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        String contactImageUri = "";

        if (cursor.moveToFirst())
        {
            if (cursor.getString(0) != null)
                contactImageUri = cursor.getString(0);
        }
        cursor.close();

        return contactImageUri;
    }
}
