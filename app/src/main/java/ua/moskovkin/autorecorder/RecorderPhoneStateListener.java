package ua.moskovkin.autorecorder;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
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
    private int notificationId = 1015;
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
                    showRecordingIcon();
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
                    hideRecordingIcon();
                    context.stopService(new Intent(context, CallRecorderService.class));
                    Log.d(Constants.DEBUG_TAG, "IDLE " + callingNumber);
                }
                break;
        }
    }

    private String getFileName(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        if (isIncomingCall) {
            return dateFormat.format(date) + "I";
        }
        return dateFormat.format(date);
    }

    private void showRecordingIcon() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_mic)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.action_recording_text));
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private void hideRecordingIcon() {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(notificationId);
    }
}
