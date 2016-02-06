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
//
//        if (callListener == null) {
//            callListener = new RecorderPhoneStateListener();
//        }
//
//        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
//            telephonyManager.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
//        }
        //serviceIntent.putExtras(intent);
        if (!CallRecorderService.isServiceRunning) {
            Log.d("Service", "Service Started");
            context.startService(new Intent(context, CallRecorderService.class));
        }
    }
}
