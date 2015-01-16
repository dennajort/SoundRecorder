package com.example.stephane.soundrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Handler;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordFragment extends Fragment {
    final static String TAG = "RecordFragment";

    private TextView timerText;
    private ImageView recordButton;

    private int mCounter = 0;
    private Handler handler = new Handler();
    private State mState = State.WAITING;
    private MediaRecorder mMediaRecorder = null;
    private File mTempFile = null;

    public enum State {
        WAITING,
        RECORDING
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mState == State.RECORDING) {
                timerText.setText(durationStringFormat(++mCounter));
                handler.postDelayed(this, 1000);
            }
        }
    };

    public RecordFragment() {

    }

    private boolean copyFile(File src, File dst) {
        FileChannel inChannel;
        FileChannel outChannel;
        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            outChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Boolean moveFile(File src, File dst) {
        Boolean ret = copyFile(src, dst);
        if (ret) {
            src.delete();
        }
        return ret;
    }

    private void startRecord() {
        if (mMediaRecorder != null) {
            try {
                mTempFile = File.createTempFile("sound_record", "3gpp");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Can't create TempFile");
                return;
            }
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(mTempFile.getPath());
            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Can't prepare MediaRecorder");
                return;
            }
            mMediaRecorder.start();
            recordButton.setImageResource(R.drawable.stop_button_play_pause_music);
            mState = State.RECORDING;
            mCounter = 0;
            timerText.setText(durationStringFormat(0));
            handler.postDelayed(runnable, 1000);
        }
    }

    private void saveToDrive(final String fileName) {
        final MainActivity activity = (MainActivity) getActivity();
        Drive.DriveApi.newDriveContents(activity.mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        BufferedOutputStream outputStream = new BufferedOutputStream(result.getDriveContents().getOutputStream());
                        // Open the file and write into stream
                        BufferedInputStream fileStream;
                        try {
                            fileStream = new BufferedInputStream(new FileInputStream(mTempFile));
                        } catch (FileNotFoundException e) {
                            Log.i(TAG, "Failed to open audio file.");
                            return;
                        }
                        try {
                            byte[] buff = new byte[32 * 1024];
                            int len;
                            while ((len = fileStream.read(buff)) > 0)
                                outputStream.write(buff, 0, len);
                            fileStream.close();
                            outputStream.close();
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("audio/3gpp").setTitle(fileName).build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(activity.mGoogleApiClient);
                        try {
                            activity.startIntentSenderForResult(intentSender, MainActivity.REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "Failed to launch file chooser.");
                        }
                    }
                });
    }

    private void endRecord() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
        final String fileName = "record_" + sdf.format(new Date()) + ".3gpp";

        String[] items = {"Local storage", "Google Drive"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose destination")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                File recordDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/records");
                                recordDirectory.mkdirs();
                                File file = new File(recordDirectory, fileName);
                                moveFile(mTempFile, file);
                                break;
                            case 1:
                                saveToDrive(fileName);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.i(TAG, "Cancel choosing dialog");
                        mTempFile.delete();
                    }
                });
        builder.create().show();
        recordButton.setImageResource(R.drawable.record_button_play_stop_music);
        mState = State.WAITING;
        handler.removeCallbacks(runnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_record, container, false);

        timerText = (TextView) rootView.findViewById(R.id.time);
        recordButton = (ImageView)rootView.findViewById(R.id.record);
        mState = State.WAITING;
        mMediaRecorder = new MediaRecorder();

        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.e("record", "click");
                switch (mState) {
                    case WAITING:
                        startRecord();
                        break;
                    case RECORDING:
                        endRecord();
                        break;
                    default:
                        break;
                }
            }
        });

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
}