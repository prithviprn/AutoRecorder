package ua.moskovkin.autorecorder;

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

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            //when incoming call
            case TelephonyManager.CALL_STATE_RINGING:
                isIncomingCall = true;
                Log.d("calling", "RINGING " + incomingNumber);
                break;
            //when speaking
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if(!callReceived && incomingNumber.length() > 0) {
                    callReceived = true;
                    String number = incomingNumber.replace("+", "");
                    String dirName;
                    if (number.length() >= 7) {
                        dirName = number.substring(number.length() - 7, number.length());
                    } else {
                        dirName = number;
                    }
                    File path = new File(MainActivity.appFolder + File.separator, dirName);
                    if(!path.exists()) {
                        path.mkdirs();
                    }
                    Date date = new Date();
                    String FILE_EXTENSION = ".3gp";
                    String fileName = getFileName(date) + FILE_EXTENSION;
                    String filePath = path + File.separator + fileName;
                    mRecorder = new CallRecorder();
                    mRecorder.setFilePath(filePath);
                    mRecorder.startRecording();
                    Log.d("calling", "OFFHOOK " + incomingNumber + " " + filePath);
                }
                break;
            //when call off
            case TelephonyManager.CALL_STATE_IDLE:
                if (callReceived && incomingNumber.length() > 0) {
                    callReceived = false;
                    mRecorder.stopRecording();
                    mRecorder = null;
                    isIncomingCall = false;
                    Log.d("calling", "IDLE " + incomingNumber);
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
