package com.example.stephane.soundrecorder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    private State mState = State.NOTHING;
    private TextView songNameText;
    private TextView songDurationText;
    private ImageView actionButton;
    private MediaPlayer mPlayer = null;

    public ControlsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_controls, container, false);

        songNameText = (TextView) rootView.findViewById(R.id.recordName);
        songDurationText = (TextView) rootView.findViewById(R.id.recordDuration);

        actionButton = (ImageView)rootView.findViewById(R.id.player_action);
        actionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if (mState == State.PLAYING) {
                    if (mPlayer != null) {
                        mState = State.PAUSE;
                        actionButton.setImageResource(R.drawable.controls_play);
                        mPlayer.pause();
                    }
                }
                else {
                    if (mPlayer != null) {
                        mState = State.PLAYING;
                        actionButton.setImageResource(R.drawable.controls_pause);
                        mPlayer.start();
                    }
                }

            }
        });

        return rootView;
    }

    public void playSong(Record record) {
        songNameText.setText(record.name);
        songDurationText.setText(record.duration);

        if (mState == State.PLAYING) {
            mPlayer.release();
        }
        mPlayer = null;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(record.path);
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    mState = State.NOTHING;
                    actionButton.setImageResource(R.drawable.controls_play);
                    if (mPlayer != null) {
                        mPlayer.seekTo(0);
                    }
                }
            });
            mPlayer.start();
            mState = State.PLAYING;
            actionButton.setImageResource(R.drawable.controls_pause);
        } catch (IOException e) {
            mState = State.NOTHING;
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