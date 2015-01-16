package com.example.stephane.soundrecorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Handler;

/**
 * Created by Stéphane on 11/01/2015.
 */

public class RecordFragment extends Fragment {

    private TextView timerText;
    private TextView filePlayingText;

    private int mCounter = 0;
    private Handler handler = new Handler();


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mCounter += 1;
            timerText.setText(durationStringFormat(mCounter));
            handler.postDelayed(this, 1000);
        }
    };

    public RecordFragment() {
        //((MainActivity)this.getActivity()).getTest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_record, container, false);

        timerText = (TextView) rootView.findViewById(R.id.time);
        filePlayingText = (TextView) rootView.findViewById(R.id.fileNamePlaying);

        ImageView playButton = (ImageView)rootView.findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Log.e("play", "click");
            }
        });

        ImageView recordButton = (ImageView)rootView.findViewById(R.id.record);
        recordButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Log.e("record", "click");
                mCounter = 0;
                handler.postDelayed(runnable, 1000);
            }
        });
        Log.e("record",((MainActivity)this.getActivity()).getFileNamePlaying());
        if (((MainActivity)this.getActivity()).getFileNamePlaying() != "") {
            filePlayingText.setText(((MainActivity)this.getActivity()).getFileNamePlaying());
        };

        return rootView;
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    String durationStringFormat(int duration) {
        int h = duration / 3600;
        int m = (duration - h * 3600) / 60;
        int s = duration - (h * 3600 + m * 60);

        String timeDuration = "";

        if (h < 10)
            timeDuration += "0";
        timeDuration += (Integer.toString(h) + ":");
        if (m < 10)
            timeDuration += "0";
        timeDuration += (Integer.toString(m) + ":");
        if (s < 10)
            timeDuration += "0";
        timeDuration += Integer.toString(s);

        return (timeDuration);
    }

    public void changeMusicPlayingText(String name) {
        filePlayingText.setText(name);
    }
}