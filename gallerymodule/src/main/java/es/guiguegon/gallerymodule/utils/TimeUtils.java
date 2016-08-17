package es.guiguegon.gallerymodule.utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by guillermoguerrero on 17/08/16.
 */
public class TimeUtils {

    private TimeUtils() {
        //empty constructor
    }

    public static String getTimeFromVideoDuration(long duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}
