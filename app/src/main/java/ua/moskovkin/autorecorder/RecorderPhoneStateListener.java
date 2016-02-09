package ua.moskovkin.autorecorder;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecorderPhoneStateListener extends PhoneStateListener {
    private boolean callReceived;
    private boolean isIncomingCall = false;
    private CallRecorder mRecorder;
    private Context context;
    private Intent intent;

    public RecorderPhoneStateListener(Context context, Intent intent) {
        super();
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        String callingNumber = intent.getStringExtra("NUMBER");
        switch (state) {
            //when incoming call
            case TelephonyManager.CALL_STATE_RINGING:
                isIncomingCall = true;
                Log.d(Constants.DEBUG_TAG, "RINGING " + callingNumber);
                break;
            //when speaking
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if(!callReceived && callingNumber.length() > 0) {
                    callReceived = true;
                    String number = callingNumber.replace("+", "");
                    String dirName;
                    if (number.length() >= Constants.DIR_LENGTH) {
                        dirName = number.substring(number.length() - Constants.DIR_LENGTH, number.length());
                    } else {
                        dirName = number;
                    }
                    File path = new File(Environment.getExternalStorageDirectory()
                            + File.separator
                            + context.getString(R.string.app_name)
                            + File.separator, dirName);
                    if(!path.exists()) {
                        path.mkdirs();
                    }
                    Date date = new Date();
                    String fileName = getFileName(date) + Constants.FILE_EXTENSION_3GP;
                    String filePath = path + File.separator + fileName;
                    mRecorder = new CallRecorder();
                    mRecorder.setFilePath(filePath);
                    mRecorder.startRecording();
                    Log.d(Constants.DEBUG_TAG, "OFFHOOK " + callingNumber + " " + filePath);
                }
                break;
            //when call off
            case TelephonyManager.CALL_STATE_IDLE:
                if (callReceived) {
                    callReceived = false;
                    mRecorder.stopRecording();
                    mRecorder = null;
                    isIncomingCall = false;
                    context.stopService(new Intent(context, CallRecorderService.class));
                    Log.d(Constants.DEBUG_TAG, "IDLE " + callingNumber);
                }
                break;
        }
    }

    private String getFileName(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        if (isIncomingCall) {
            return "I" + dateFormat.format(date);
        }
        return dateFormat.format(date);
    }
}
