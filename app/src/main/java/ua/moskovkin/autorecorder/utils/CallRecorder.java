package ua.moskovkin.autorecorder.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

public class CallRecorder {
    private MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;
    private String mFilePath;

    public CallRecorder() {
        mRecorder = new MediaRecorder();
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
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
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

}
