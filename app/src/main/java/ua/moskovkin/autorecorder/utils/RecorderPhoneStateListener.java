package ua.moskovkin.autorecorder.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.service.CallRecorderService;

public class RecorderPhoneStateListener extends PhoneStateListener {
    private boolean callReceived;
    private boolean isIncomingCall = false;
    private int notificationId = 1015;
    private long minDuration;
    private CallRecorder mRecorder;
    private SharedPreferences settings;
    private Context context;
    private Intent intent;
    private MediaMetadataRetriever mmr;

    public RecorderPhoneStateListener(Context context, Intent intent) {
        super();
        this.context = context;
        this.intent = intent;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        minDuration = Long.parseLong(settings.getString("record_duration", Constants.MIN_DURATION));
        mmr = new MediaMetadataRetriever();
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
                    File path = new File(settings.getString("app_save_path",
                            Environment.getExternalStorageDirectory().getAbsolutePath()), dirName);
                    if(!path.exists()) {
                        path.mkdirs();
                    }
                    Date date = new Date();
                    String fileName = getFileName(date) + getFileExtension();
                    String filePath = path + File.separator + fileName;
                    mRecorder = new CallRecorder(context);
                    mRecorder.setFilePath(filePath);
                    mRecorder.startRecording();
                    Log.d(Constants.DEBUG_TAG, "OFFHOOK " + callingNumber + " " + filePath);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (callReceived) {
                    hideRecordingIcon();
                    callReceived = false;
                    mRecorder.stopRecording();
                    mmr.setDataSource(mRecorder.getFilePath());
                    long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
                    if (duration <= minDuration) {
                        File file = new File(mRecorder.getFilePath());
                        file.delete();
                    }
                    mRecorder = null;
                    isIncomingCall = false;
                    context.stopService(new Intent(context, CallRecorderService.class));
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

    private String getFileExtension() {
        String extension = settings.getString("audio_format", "1");
        String fileExtension = null;
        switch (extension) {
            case "1": {
                fileExtension = ".3gp";
                break;
            }
            case "2": {
                fileExtension = ".mp4";
                break;
            }
            case "3": {
                fileExtension = ".amr";
                break;
            }
            case "4": {
                fileExtension = ".amr";
                break;
            }
            case "6": {
                fileExtension = ".aac";
                break;
            }
            case "9": {
                fileExtension = ".webm";
                break;
            }
        }
        return fileExtension;
    }
}
