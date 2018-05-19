package com.techweblearn.mediastreaming.Utils;

public class Utils {

    public static long calculateDownloadedInSec(float downloadProgress, long size, int bitrate, long duration)
    {
        long result=(long) ((size*8/100)/(bitrate)*downloadProgress*1000);
        return result;
    }
}
