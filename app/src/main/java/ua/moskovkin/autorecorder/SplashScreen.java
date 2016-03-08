package ua.moskovkin.autorecorder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;
import java.util.GregorianCalendar;

import ua.moskovkin.autorecorder.utils.DBHelper;

public class SplashScreen extends Activity{
    private static final int SPLASH_TIME_OUT = 2000;
    private final static int PASS_REQUEST = 12;
    private File appFolder;
    private DBHelper dbHelper;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        dbHelper = new DBHelper(this);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        if (settings.getString(Constants.SETTING_APP_SAVE_PATH_KEY, "empty").equals("empty")) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
                settings.edit().putString(Constants.SETTING_APP_SAVE_PATH_KEY, Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                        + File.separator + getString(R.string.app_name)).apply();
            } else {
                settings.edit().putString(Constants.SETTING_APP_SAVE_PATH_KEY, Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + getString(R.string.app_name)).apply();
            }
        }
        appFolder = new File(settings.getString(Constants.SETTING_APP_SAVE_PATH_KEY,
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + getString(R.string.app_name)));
        if(!appFolder.exists()) {
            try {
                appFolder.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dbHelper.addContactNumbersAndRecordsToDb(appFolder.getPath());
        dbHelper.deleteNonExistingRecords();
        dbHelper.deleteEmptyContacts();

        int maxDays = Integer.parseInt(settings.getString(Constants.SETTING_DELETE_RECORDS_OLDER_THAN_KEY, "0"));
        if (maxDays != 0) {
            dbHelper.deleteRecordsOlderThan(GregorianCalendar.getInstance(), maxDays);
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (settings.getBoolean(Constants.SETTING_PASS_PROTECTION_KEY, false)) {
                    Intent i = new Intent(getApplication(), PassActivity.class);
                    startActivityForResult(i, PASS_REQUEST);
                } else {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PASS_REQUEST) {
            Toast.makeText(this, R.string.access_granted, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);

            finish();
        } else {
            finish();
        }
    }
}
