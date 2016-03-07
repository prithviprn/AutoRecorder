package ua.moskovkin.autorecorder.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.service.CallRecorderService;
import ua.moskovkin.autorecorder.utils.DBHelper;

public class CallRecorderBroadcastReceiver extends BroadcastReceiver {
    private SharedPreferences setting;
    private DBHelper dbHelper;

    @Override
    public void onReceive(final Context context, Intent intent) {
        setting = PreferenceManager.getDefaultSharedPreferences(context);
        dbHelper = new DBHelper(context);
        String number;
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        } else {
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        }
        if (setting.getBoolean(Constants.IS_RECORDING_ON, false)) {
            startRecordingService(context, number);
        } else if (dbHelper.isNumberInExcludedList(number)) {
            startRecordingService(context, number);
        }
    }

    private void startRecordingService(Context context, String number) {
        Intent serviceIntent = new Intent(context, CallRecorderService.class);
        serviceIntent.putExtra("NUMBER", number);

        if (serviceIntent.getStringExtra("NUMBER") != null) {
            if (!CallRecorderService.isServiceRunning) {
                context.startService(serviceIntent);
            }
        }
    }
}
