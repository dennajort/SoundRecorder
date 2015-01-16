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
    private PlayerFragment playerFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            playerFragment = (PlayerFragment) fragmentManager.getFragment(savedInstanceState, "playerFragment");
            recordFragment = (RecordFragment) fragmentManager.getFragment(savedInstanceState, "recordFragment");
        }
        if (playerFragment == null) {
            Log.i("MainActivity", "new playerFragment");
            playerFragment = new PlayerFragment();
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
        if (!playerFragment.isAdded()) {
            fragmentManager.beginTransaction().replace(R.id.container2, playerFragment).commit();
        }

        //if (findViewById(R.id.main_land) == null) {
            mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
            mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        //}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, "recordFragment", recordFragment);
        fragmentManager.putFragment(outState, "playerFragment", playerFragment);
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
                fragmentManager.beginTransaction().hide(playerFragment).show(recordFragment).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().hide(recordFragment).show(playerFragment).commit();
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
        /*
        if (mState == State.PLAYING) {
            mPlayer.release();
        }
        mPlayer = null;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    mState = State.NOTHING;
                    if (mPlayer != null) {
                        mPlayer.release(); }
                }
            });
            mPlayer.start();
            mState = State.PLAYING;
        } catch (IOException e) {
            Log.e("playSongPath", "prepare() failed");
        }
        */
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
