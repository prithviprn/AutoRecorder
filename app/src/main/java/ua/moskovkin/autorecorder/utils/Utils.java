package ua.moskovkin.autorecorder.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import java.util.GregorianCalendar;

import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.fragments.CustomAudioPlayer;

public class Utils {

    public static GregorianCalendar getCalendarFromFile(String[] path) {
        GregorianCalendar calendar = new GregorianCalendar(
                Integer.parseInt(path[path.length - 1].substring(0, 4)),
                Integer.parseInt(path[path.length - 1].substring(4, 6)) - 1,
                Integer.parseInt(path[path.length - 1].substring(6, 8)),
                Integer.parseInt(path[path.length - 1].substring(8, 10)),
                Integer.parseInt(path[path.length - 1].substring(10, 12)),
                Integer.parseInt(path[path.length - 1].substring(12, 14)));

        return calendar;
    }

    public static boolean isContactNameInContacts(String contactNumber, Context context) {
        return !Utils.getContactName(contactNumber, context).equals("");
    }

    public static boolean isContactImageSet(String contactNumber, Context context) {
        return !Utils.getContactImage(contactNumber, context).equals("");
    }

    public static void playRecord(String path, boolean isInternalPlayer, FragmentManager fm, Context context) {
        if (isInternalPlayer) {
            CustomAudioPlayer player = new CustomAudioPlayer();
            player.setPath(path);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_slide_up_start, R.anim.fragment_slide_up_end)
                    .replace(R.id.player_container, player, "player").commit();
        } else {
            Uri uri = Uri.parse(path);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.setDataAndType(uri, "video/3gpp");
            Intent chooser = Intent.createChooser(i, context.getString(R.string.select_player));
            if (i.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(chooser);
            } else {
                Toast.makeText(context, R.string.no_player, Toast.LENGTH_SHORT).show();
            }
        }
    }

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
