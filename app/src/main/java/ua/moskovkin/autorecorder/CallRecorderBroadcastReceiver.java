package ua.moskovkin.autorecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallRecorderBroadcastReceiver extends BroadcastReceiver {
    private static RecorderPhoneStateListener callListener;
    private TelephonyManager telephonyManager;

    @Override
    public void onReceive(final Context context, Intent intent) {
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (callListener == null) {
            callListener = new RecorderPhoneStateListener();
        }

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            telephonyManager.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}
