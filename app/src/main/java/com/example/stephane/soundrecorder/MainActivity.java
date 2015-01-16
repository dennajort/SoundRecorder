package com.example.stephane.soundrecorder;

import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    public enum State {
        PLAYING,
        RECORDING,
        NOTHING
    }

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private State mState = State.NOTHING;
    private String mFileNamePlaying = "";

    private RecordFragment recordFragment = null;
    private AllRecordsFragment allRecordsFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            mState = State.values()[savedInstanceState.getInt("mState")];
            allRecordsFragment = (AllRecordsFragment) fragmentManager.getFragment(savedInstanceState, "allRecordsFragment");
            recordFragment = (RecordFragment) fragmentManager.getFragment(savedInstanceState, "recordFragment");
        }
        if (allRecordsFragment == null) {
            Log.i("MainActivity", "new AllRecordsFragment");
            allRecordsFragment = new AllRecordsFragment();
        }
        if (recordFragment == null) {
            Log.i("MainActivity", "new RecordFragment");
            recordFragment = new RecordFragment();
        }

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (!recordFragment.isAdded()) {
            fragmentManager.beginTransaction().replace(R.id.container, recordFragment).commit();
        }
        if (!allRecordsFragment.isAdded()) {
            fragmentManager.beginTransaction().replace(R.id.container2, allRecordsFragment).commit();
        }

        if (findViewById(R.id.main_land) == null) {
            mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
            mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("mState", mState.ordinal());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, "recordFragment", recordFragment);
        fragmentManager.putFragment(outState, "allRecordsFragment", allRecordsFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction().hide(allRecordsFragment).show(recordFragment).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().hide(recordFragment).show(allRecordsFragment).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    public void playSongPath(String filePath) {
        CommonData common = CommonData.getInstance();
        if (mState == State.PLAYING) {
            common.mPlayer.release();
        }

        common.mPlayer = new MediaPlayer();
        common.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            common.mPlayer.setDataSource(filePath);
            common.mPlayer.prepare();
            common.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    mState = State.NOTHING;
                    CommonData common = CommonData.getInstance();
                    if (common.mPlayer != null) {
                        common.mPlayer.release();
                    }
                }
            });
            common.mPlayer.start();
            mState = State.PLAYING;
        } catch (IOException e) {
            Log.e("playSongPath", "prepare() failed");
        }
    }

    public State getStatus() {
        return this.mState;
    }

    public void setStatus(State status) {
        this.mState = status;
    }

    public String getFileNamePlaying() {
        return this.mFileNamePlaying;
    }

    public void setFileNamePlaying(String name) {
        this.mFileNamePlaying = name;
        if (recordFragment != null) {
            recordFragment.changeMusicPlayingText(name);
        }
    }
}
