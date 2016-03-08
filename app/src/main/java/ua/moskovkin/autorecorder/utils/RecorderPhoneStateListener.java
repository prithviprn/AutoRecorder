package ua.moskovkin.autorecorder.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
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
        minDuration = Long.parseLong(settings.getString(Constants.SETTING_MIN_RECORD_DURATION_KEY, Constants.MIN_RECORD_DURATION));
        mmr = new MediaMetadataRetriever();
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        String callingNumber = null;
        try {
            callingNumber = intent.getStringExtra("NUMBER");
        } catch (NullPointerException e) {
            e.printStackTrace();
            context.stopService(new Intent(context, CallRecorderService.class));
        }
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
                    File path = new File(settings.getString(Constants.SETTING_APP_SAVE_PATH_KEY,
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
                Log.d(Constants.DEBUG_TAG, "IDLE INTENT");
                if (callReceived) {
                    hideRecordingIcon();
                    callReceived = false;
                    mRecorder.stopRecording();
                    mmr.setDataSource(mRecorder.getFilePath());
                    if (minDuration != 0) {
                        long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
                        if (duration <= minDuration) {
                            File file = new File(mRecorder.getFilePath());
                            file.delete();
                        }
                    }
                    if (settings.getBoolean(Constants.SETTING_ASK_TO_SAVE_KEY, false)) {
                        Intent intent = new Intent(context, SaveRecordDialogActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("filePath", mRecorder.getFilePath());
                        context.startActivity(intent);
                    }
                    mRecorder = null;
                    isIncomingCall = false;
                    context.stopService(new Intent(context, CallRecorderService.class));
                } else {
                    context.stopService(new Intent(context, CallRecorderService.class));
                    isIncomingCall = false;
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
        String extension = settings.getString(Constants.SETTING_AUDIO_FORMAT_KEY, "1");
        String fileExtension = null;
        switch (extension) {
            case "1": {
                fileExtension = Constants.EXTENSION_3GP;
                break;
            }
            case "2": {
                fileExtension = Constants.EXTENSION_MP4;
                break;
            }
            case "3": {
                fileExtension = Constants.EXTENSION_AMR;
                break;
            }
            case "6": {
                fileExtension = Constants.EXTENSION_AAC;
                break;
            }
        }
        return fileExtension;
    }
}
