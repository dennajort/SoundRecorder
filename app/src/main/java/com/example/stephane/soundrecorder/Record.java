package com.example.stephane.soundrecorder;

import android.media.MediaMetadataRetriever;

import java.io.File;

/**
 * Created by Stéphane on 14/01/2015.
 */

public class Record {
    public String   path;
    public String   name;
    public String   duration;

    public Record(String path, String name, int duration) {
        this.path = path;
        this.name = name;
        this.duration = Integer.toString(duration);
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