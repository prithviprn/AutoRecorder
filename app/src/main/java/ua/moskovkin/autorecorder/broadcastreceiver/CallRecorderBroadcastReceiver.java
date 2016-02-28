package ua.moskovkin.autorecorder.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.service.CallRecorderService;

public class CallRecorderBroadcastReceiver extends BroadcastReceiver {
    private SharedPreferences setting;

    @Override
    public void onReceive(final Context context, Intent intent) {
        setting = PreferenceManager.getDefaultSharedPreferences(context);
        if (setting.getBoolean(Constants.IS_RECORDING_ON, false)) {
            Intent serviceIntent = new Intent(context, CallRecorderService.class);
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                serviceIntent.putExtra("NUMBER", intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                Log.d(Constants.DEBUG_TAG, intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) + " Outgoing Intent Number");
            } else {
                serviceIntent.putExtra("NUMBER", intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
            }

            if (serviceIntent.getStringExtra("NUMBER") != null) {
                if (!CallRecorderService.isServiceRunning) {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
