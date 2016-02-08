package ua.moskovkin.autorecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mediatek.telephony.TelephonyManagerEx;

public class CallRecorderService extends Service {
    private static RecorderPhoneStateListener callListener0;
    private static RecorderPhoneStateListener callListener1;
    public static boolean isServiceRunning;
    private boolean isDualSim;
    private TelephonyManager telephonyManager;
    private TelephonyManagerEx mtkManager0;
    private TelephonyManagerEx mtkManager1;

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
        isDualSim = false;
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mtkManager0 = null;
        Log.d(Constants.DEBUG_TAG, "Service Created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mtkManager0 == null) {
            callListener0 = new RecorderPhoneStateListener(this, intent);
            telephonyManager.listen(callListener0, PhoneStateListener.LISTEN_CALL_STATE);
            Log.d(Constants.DEBUG_TAG, "Single Service Started");
        } else {
            isDualSim = true;
            try {
                mtkManager0 = new TelephonyManagerEx(this);
                mtkManager1 = new TelephonyManagerEx(this);
                callListener1 = new RecorderPhoneStateListener(this, intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mtkManager0.listen(callListener0, PhoneStateListener.LISTEN_CALL_STATE, 0);
            mtkManager1.listen(callListener1, PhoneStateListener.LISTEN_CALL_STATE, 1);
            Log.d(Constants.DEBUG_TAG, "MultiSim Service Started");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constants.DEBUG_TAG, "Service Destroyed");
        isServiceRunning = false;
        if (!isDualSim) {
            telephonyManager.listen(callListener0, PhoneStateListener.LISTEN_NONE);
        } else {
            mtkManager0.listen(callListener0, PhoneStateListener.LISTEN_NONE, 0);
            mtkManager1.listen(callListener1, PhoneStateListener.LISTEN_NONE, 1);
        }
    }
}
