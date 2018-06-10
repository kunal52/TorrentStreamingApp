package com.techweblearn.mediastreaming.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.techweblearn.mediastreaming.Utils.Constants.BUFFER_DURATION;
import static com.techweblearn.mediastreaming.Utils.Constants.IS_STREAMIMG;
import static com.techweblearn.mediastreaming.Utils.Constants.MOVIE_NAME;
import static com.techweblearn.mediastreaming.Utils.Constants.PROGRESS_DURATION;

public class Preferences {



    public static void  setPlayingVideoName(Context context,String name)
    {
        SharedPreferences.Editor sharedPreferences=context.getSharedPreferences("stream",Context.MODE_PRIVATE).edit().putString(MOVIE_NAME,name);
        sharedPreferences.apply();
    }


    public static void setProgressDuration(Context context,int progressDuration)

    {
        SharedPreferences.Editor sharedPreferences=context.getSharedPreferences("stream",Context.MODE_PRIVATE).edit().putInt(PROGRESS_DURATION,progressDuration);
        sharedPreferences.apply();
    }



    public static void setBufferDuration(Context context,int bufferDuration)

    {
        SharedPreferences.Editor sharedPreferences=context.getSharedPreferences("stream",Context.MODE_PRIVATE).edit().putInt(BUFFER_DURATION,bufferDuration);
        sharedPreferences.apply();
    }


    public static void isStreaming(Context context,boolean is)
    {
        SharedPreferences.Editor editor=context.getSharedPreferences("stream",Context.MODE_PRIVATE).edit().putBoolean(IS_STREAMIMG,is);
        editor.apply();
    }

    public static String getPlayingVideoName(Context context)
    {
        return context.getSharedPreferences("stream",Context.MODE_PRIVATE).getString(Constants.MOVIE_NAME,"");
    }

    public static int getBufferDuration(Context context)
    {
        return context.getSharedPreferences("stream",Context.MODE_PRIVATE).getInt(Constants.BUFFER_DURATION,0);
    }

    public static int getProgressDuration(Context context)
    {
        return context.getSharedPreferences("stream",Context.MODE_PRIVATE).getInt(Constants.PROGRESS_DURATION,0);
    }


    public static boolean isStreaming(Context context)
    {
        return context.getSharedPreferences("stream",Context.MODE_PRIVATE).getBoolean(Constants.IS_STREAMIMG,false);
    }


}
