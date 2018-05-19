package com.techweblearn.mediastreaming.Playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;


import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.Models.PlayerInfo;
import com.techweblearn.mediastreaming.Models.VideoInfo;
import com.techweblearn.mediastreaming.Streaming.StreamStatusExtended;
import com.techweblearn.mediastreaming.Utils.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

public class StreamLoadController extends DefaultLoadControl  {


    public static final String TAG=StreamLoadController.class.getSimpleName();

    private StreamStatus streamStatus;
    private PlayerInfo playerInfo;
    private VideoInfo videoInfo;


    public StreamLoadController(Context context)
    {
    }


    @Override
    public boolean shouldContinueLoading(long bufferedDurationUs, float playbackSpeed) {


        try {


            if(streamStatus.progress==100)
                return super.shouldContinueLoading(bufferedDurationUs, playbackSpeed);

        return playerInfo.getBufferedProgress() + 25000 <= Utils.calculateDownloadedInSec(streamStatus.progress
                , videoInfo.getTotalSize()
                , videoInfo.getBitrate()
                , videoInfo.getDuration())
                && super.shouldContinueLoading(bufferedDurationUs, playbackSpeed);
        }catch (Exception e){
           // e.printStackTrace();
            return false;
        }

    }

    private boolean isDownloadedForBuffering(long buffered,float downloaded, long size, int bitrate)
    {
        return false;
    }



    public void setVideoInfo(VideoInfo videoInfo)
    {
        this.videoInfo=videoInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo)
    {
        this.playerInfo=playerInfo;
    }

    public void setStreamStatus(StreamStatus streamStatus)
    {
        this.streamStatus=streamStatus;
    }


}
