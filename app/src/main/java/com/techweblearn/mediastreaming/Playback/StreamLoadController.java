package com.techweblearn.mediastreaming.Playback;


import android.util.Log;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.techweblearn.mediastreaming.Models.PlayerInfo;
import com.techweblearn.mediastreaming.Models.VideoInfo;
import com.techweblearn.mediastreaming.Streaming.StreamStatusExtended;
import com.techweblearn.mediastreaming.Utils.Utils;


public class StreamLoadController extends DefaultLoadControl  {


    public static final String TAG=StreamLoadController.class.getSimpleName();


    private StreamStatus streamStatus;
    private PlayerInfo playerInfo;
    private VideoInfo videoInfo;
    private StreamStatusExtended streamStatusExtended;



    @Override
    public boolean shouldContinueLoading(long bufferedDurationUs, float playbackSpeed) {

        Log.d(TAG, String.valueOf(bufferedDurationUs));
        try {

            //TODO  CHANGE THIS

            if(streamStatusExtended.getProgress()==100)
                return super.shouldContinueLoading(bufferedDurationUs, playbackSpeed);



          return isDownloadedForBuffering(playerInfo.getBufferedProgress(),Utils.calculateDownloadedInMS(videoInfo.getBitrate(),streamStatusExtended.getDownloadedBytes()))
                  && super.shouldContinueLoading(bufferedDurationUs, playbackSpeed);


          //  return super.shouldContinueLoading(bufferedDurationUs,playbackSpeed);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    private boolean isDownloadedForBuffering(long buffered,long downloaded)
    {

        Log.d(TAG,"Buffered : "+buffered);
        Log.d(TAG,"Downloaded : "+downloaded);
        return buffered + 45000 < downloaded;

    }

    private boolean isDownloadForBuffering(long buffered,long downloaded)
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

    public void setStreamStatusExtended(StreamStatusExtended streamStatusExtended)
    {
        this.streamStatusExtended=streamStatusExtended;
    }


}
