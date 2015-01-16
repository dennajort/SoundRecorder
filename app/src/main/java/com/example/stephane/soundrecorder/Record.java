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

    public Record(String path, String name, long duration) {
        this.path = path;
        this.name = name;
        this.duration = durationStringFormat(duration / 1000);
    }


    String durationStringFormat(long duration) {

        long h = duration / 3600;
        long m = (duration - h * 3600) / 60;
        long s = duration - (h * 3600 + m * 60);

        String timeDuration = "";

        if (h < 10)
            timeDuration += "0";
        timeDuration += (Long.toString(h) + ":");
        if (m < 10)
            timeDuration += "0";
        timeDuration += (Long.toString(m) + ":");
        if (s < 10)
            timeDuration += "0";
        timeDuration += Long.toString(s);

        return (timeDuration);
    }
}