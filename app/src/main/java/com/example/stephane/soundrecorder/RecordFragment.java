package com.example.stephane.soundrecorder;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Stéphane on 11/01/2015.
 */

public class RecordFragment extends Fragment {
    final static String TAG = "RecordFragment";

    private TextView timerText;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_record, container, false);

        timerText = (TextView) rootView.findViewById(R.id.time);
        mState = State.WAITING;
        mMediaRecorder = new MediaRecorder();

        final ImageView recordButton = (ImageView)rootView.findViewById(R.id.record);
        recordButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Log.e("record", "click");
                switch (mState) {
                    case WAITING:
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
                        break;
                    case RECORDING:
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
                        String fileName = "record_" + sdf.format(new Date()) + ".3gpp";
                        File recordDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/records");
                        recordDirectory.mkdirs();
                        File file = new File(recordDirectory, fileName);
                        moveFile(mTempFile, file);
                        recordButton.setImageResource(R.drawable.record_button_play_stop_music);
                        mState = State.WAITING;
                        handler.removeCallbacks(runnable);
                        break;
                    default:
                        break;
                }
            }
        });
        Log.e("record",((MainActivity)this.getActivity()).getFileNamePlaying());

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