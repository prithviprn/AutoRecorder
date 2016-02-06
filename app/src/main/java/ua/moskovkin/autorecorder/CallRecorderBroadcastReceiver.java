package ua.moskovkin.autorecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallRecorderBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (!CallRecorderService.isServiceRunning) {
            context.startService(new Intent(context, CallRecorderService.class));
        }
    }
}
