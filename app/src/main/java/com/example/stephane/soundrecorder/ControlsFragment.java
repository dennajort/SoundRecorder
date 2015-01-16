package com.example.stephane.soundrecorder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Stéphane on 11/01/2015.
 */

public class ControlsFragment extends Fragment {
    public enum State {
        PLAYING,
        PAUSE,
        NOTHING
    }


    private static class Data {
        private static Data instance = null;


        public static Data getInstance() {
            if (instance == null) {
                instance = new Data();
            }
            return instance;
        }

        private Data() {

        }
        private State mState = State.NOTHING;
        private MediaPlayer mPlayer = null;
        public String songName = "";
        public String songDuration = "";
    }

    private TextView songNameText;
    private TextView songDurationText;
    private ImageView actionButton;
    private Data data = null;

    public ControlsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = Data.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_controls, container, false);

        songNameText = (TextView) rootView.findViewById(R.id.recordName);
        songDurationText = (TextView) rootView.findViewById(R.id.recordDuration);

        songNameText.setText(data.songName);
        songDurationText.setText(data.songDuration);

        actionButton = (ImageView)rootView.findViewById(R.id.player_action);
        if (data.mState == State.PLAYING) {
            actionButton.setImageResource(R.drawable.controls_pause);
        }

        actionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if (data.mState == State.PLAYING) {
                    if (data.mPlayer != null) {
                        data.mState = State.PAUSE;
                        actionButton.setImageResource(R.drawable.controls_play);
                        data.mPlayer.pause();
                    }
                }
                else {
                    if (data.mPlayer != null) {
                        data.mState = State.PLAYING;
                        actionButton.setImageResource(R.drawable.controls_pause);
                        data.mPlayer.start();
                    }
                }

            }
        });

        return rootView;
    }

    public void playSong(Record record) {
        songNameText.setText(record.name);
        songDurationText.setText(record.duration);
        data.songName = record.name;
        data.songDuration = record.duration;

        if (data.mState == State.PLAYING) {
            data.mPlayer.release();
        }
        data.mPlayer = null;
        data.mPlayer = new MediaPlayer();
        data.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            data.mPlayer.setDataSource(record.path);
            data.mPlayer.prepare();
            data.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    data.mState = State.NOTHING;
                    actionButton.setImageResource(R.drawable.controls_play);
                    if (data.mPlayer != null) {
                        data.mPlayer.seekTo(0);
                    }
                }
            });
            data.mPlayer.start();
            data.mState = State.PLAYING;
            actionButton.setImageResource(R.drawable.controls_pause);
        } catch (IOException e) {
            data.mState = State.NOTHING;
            actionButton.setImageResource(R.drawable.controls_play);
            Log.e("playSongPath", "prepare() failed");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}