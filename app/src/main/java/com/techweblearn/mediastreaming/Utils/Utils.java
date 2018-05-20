package com.techweblearn.mediastreaming.Utils;

import android.util.Log;

public class Utils {

    public static final String TAG=Utils.class.getSimpleName();

    public static final int DOWNLOADED_THRESHHOLD=2;
    public static long calculateDownloadedInMS(float downloadProgress, long size, int bitrate, long duration)
    {

        Log.d(TAG, String.valueOf(downloadProgress));
        Log.d(TAG, String.valueOf(size));
        Log.d(TAG, String.valueOf(bitrate));
        Log.d(TAG,String.valueOf(duration));

        long result=(long) ((size*8/100)/(bitrate)*downloadProgress*1000);

        result= (long) (duration*((downloadProgress-DOWNLOADED_THRESHHOLD)/100));
        Log.d("Downloaded", String.valueOf(result));
        return result;
    }

    public static long msToS(long ms)
    {
        return ms/1000;
    }


}
