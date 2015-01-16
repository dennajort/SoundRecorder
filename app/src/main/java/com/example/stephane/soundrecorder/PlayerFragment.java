package com.example.stephane.soundrecorder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Console;

/**
 * Created by Stéphane on 11/01/2015.
 */

public class PlayerFragment extends Fragment {



    private MediaPlayer mPlayer = null;
    private AllRecordsFragment fragmentAllRecords = null;
    private ControlsFragment fragmentControls = null;

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("playerfragment", "onCreate");

        fragmentAllRecords = new AllRecordsFragment();
        fragmentControls = new ControlsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        Log.i("playerfragment", "onCreateView");

        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_all_records, fragmentAllRecords)
                .replace(R.id.fragment_controls, fragmentControls)
                .commit();

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public AllRecordsFragment getFragmentAllRecords() {
        return this.fragmentAllRecords;
    }

    public ControlsFragment getFragmentControls() {
        return this.fragmentControls;
    }
}