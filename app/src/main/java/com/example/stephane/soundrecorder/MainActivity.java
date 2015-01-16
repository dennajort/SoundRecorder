package com.example.stephane.soundrecorder;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    public enum State {
        PLAYING,
        RECORDING,
        NOTHING
    }

    public static final int REQUEST_CODE_CREATOR = 1;
    public static final int REQUEST_CODE_RESOLUTION = 2;

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private State mState = State.NOTHING;
    private String mFileNamePlaying = "";

    private RecordFragment recordFragment = null;
    private PlayerFragment playerFragment = null;
    public GoogleApiClient mGoogleApiClient = null;
    private Boolean shouldInitGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shouldInitGoogle = true;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            playerFragment = (PlayerFragment) fragmentManager.getFragment(savedInstanceState, "playerFragment");
            recordFragment = (RecordFragment) fragmentManager.getFragment(savedInstanceState, "recordFragment");
            shouldInitGoogle = savedInstanceState.getBoolean("shouldInitGoogle");
        }
        if (playerFragment == null) {
            Log.i("MainActivity", "new playerFragment");
            playerFragment = new PlayerFragment();
        }
        if (recordFragment == null) {
            Log.i("MainActivity", "new RecordFragment");
            recordFragment = new RecordFragment();
        }

        if (shouldInitGoogle) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
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

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, "recordFragment", recordFragment);
        fragmentManager.putFragment(outState, "playerFragment", playerFragment);
        outState.putBoolean("shouldInitGoogle", shouldInitGoogle);
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
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                switch (resultCode) {
                    case RESULT_CANCELED:
                        if (mGoogleApiClient != null) {
                            mGoogleApiClient.disconnect();
                            mGoogleApiClient = null;
                        }
                        shouldInitGoogle = false;
                        break;
                    case RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        Log.e("LOL", Integer.toString(result.getErrorCode()));
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            // Unable to resolve, message user appropriately
        }
    }

    public PlayerFragment getPlayerFragment() {
        return this.playerFragment;
    }
}
