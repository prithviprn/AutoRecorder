package ua.moskovkin.autorecorder.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import ua.moskovkin.autorecorder.R;

public class CustomAudioPlayer extends Fragment {
    private String path;
    private TextView currentTimeTextview;
    private TextView durationTextView;
    private ImageView actionButton;
    private SeekBar seekBar;
    private MediaPlayer player;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Uri uri = Uri.parse(path);
        player = MediaPlayer.create(getActivity(),uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_player, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        actionButton = (ImageView) view.findViewById(R.id.action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!player.isPlaying()) {
                    actionButton.setImageResource(R.drawable.ic_pause);
                    player.start();
                    seekBarUpdate();
                } else {
                    actionButton.setImageResource(R.drawable.ic_play_arrow);
                    player.pause();
                }
            }
        });

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setMax(player.getDuration());
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });
        currentTimeTextview = (TextView) view.findViewById(R.id.current_time_play);
        currentTimeTextview.setText("0");
        durationTextView = (TextView) view.findViewById(R.id.duration_time);
        durationTextView.setText(milliSecondsToTimer(player.getDuration()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (player.isPlaying()) {
            player.pause();
            actionButton.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private void seekBarUpdate() {
        seekBar.setProgress(player.getCurrentPosition());
        currentTimeTextview.setText(milliSecondsToTimer(player.getCurrentPosition()));


        if (player.isPlaying()) {
            Runnable notification = new Runnable() {
                @Override
                public void run() {
                    seekBarUpdate();
                }
            };
            handler.postDelayed(notification, 1000);
        } else {
            player.pause();
            actionButton.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private void seekChange(View v) {
            SeekBar sb = (SeekBar) v;
            player.seekTo(sb.getProgress());
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
