package com.example.stephane.soundrecorder;

import android.media.MediaPlayer;

/**
 * Created by Jean-Baptiste on 16/01/2015.
 */

public class CommonData {
    private static CommonData instance = null;

    public MediaPlayer mPlayer = null;

    public static CommonData getInstance() {
        if (instance == null) {
            instance = new CommonData();
        }
        return instance;
    }

    private CommonData() {

    }
}
