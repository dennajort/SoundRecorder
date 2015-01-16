package com.example.stephane.soundrecorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

        final ImageView actionButton = (ImageView)rootView.findViewById(R.id.player_action);
        actionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if (mState == State.PLAYING) {
                    mState = State.PAUSE;
                    actionButton.setImageResource(R.drawable.controls_pause);
                }
                else {
                    mState = State.PLAYING;
                    actionButton.setImageResource(R.drawable.controls_play);
                }

            }
        });

        return rootView;
    }

    public void playSong(Record record) {
        songNameText.setText(record.name);
        songDurationText.setText(record.duration);
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