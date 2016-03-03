package ua.moskovkin.autorecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;

import ua.moskovkin.autorecorder.Constants;

public class CallRecorder {
    private MediaRecorder mRecorder = null;
    private String mFilePath;
    private SharedPreferences settings;

    public CallRecorder(Context context) {
        mRecorder = new MediaRecorder();
        settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(Integer.parseInt(settings.getString(Constants.SETTING_AUDIO_SOURCE_KEY, "1")));
        mRecorder.setOutputFormat(Integer.parseInt(settings.getString(Constants.SETTING_AUDIO_FORMAT_KEY, "1")));
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public String getFilePath() {
        return mFilePath;
    }
}
