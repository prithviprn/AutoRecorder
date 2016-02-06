package ua.moskovkin.autorecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallRecorderService extends Service {
    private RecorderPhoneStateListener callListener;
    private TelephonyManager telephonyManager;
    public static boolean isServiceRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (callListener == null) {
            callListener = new RecorderPhoneStateListener(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        telephonyManager.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
    }
}
