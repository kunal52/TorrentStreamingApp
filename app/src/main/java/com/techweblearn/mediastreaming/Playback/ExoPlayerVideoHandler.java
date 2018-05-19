package com.techweblearn.mediastreaming.Playback;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.techweblearn.mediastreaming.Models.VideoInfo;
import com.techweblearn.mediastreaming.Streaming.StreamStatusExtended;

import java.util.EventListener;

public class ExoPlayerVideoHandler
{
    private static ExoPlayerVideoHandler instance;

    public static ExoPlayerVideoHandler getInstance(){
        if(instance == null){
            instance = new ExoPlayerVideoHandler();
        }
        return instance;
    }

    private SimpleExoPlayer player;
    private Uri playerUri;
    private boolean isPlayerPlaying;
    private StreamLoadController streamLoadController;

    private ExoPlayerVideoHandler(){}

    public void prepareExoPlayerForUri(Context context, Uri uri,
                                       PlayerView exoPlayerView){
        if(context != null && uri != null && exoPlayerView != null){
            if(!uri.equals(playerUri) || player == null){
                // Create a new player if the player is null or
                // we want to play a new video
                playerUri = uri;
                streamLoadController=new StreamLoadController(context);

                player= ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(context),new DefaultTrackSelector(),streamLoadController);

                DataSource.Factory data=new DefaultDataSourceFactory(context,"MediaStreaming");
                ExtractorMediaSource.Factory vidoSource = new ExtractorMediaSource.Factory(data);
                MediaSource mediaSource=vidoSource.createMediaSource(playerUri,null,null);



                player.prepare(mediaSource);
            }
            player.clearVideoSurface();
            player.setVideoSurfaceView(
                    (SurfaceView)exoPlayerView.getVideoSurfaceView());
            player.seekTo(player.getCurrentPosition() + 1);
            exoPlayerView.setPlayer(player);
            player.setPlayWhenReady(true);


        }
    }



    public void addListener(SimpleExoPlayer.EventListener listener)
    {
        player.addListener(listener);
    }


    public void releaseVideoPlayer(){
        if(player != null)
        {
            player.release();
        }
        player = null;
    }

    public void goToBackground(){
        if(player != null){
            isPlayerPlaying = player.getPlayWhenReady();
            player.setPlayWhenReady(false);
        }
    }

    public void goToForeground(){
        if(player != null){
            player.setPlayWhenReady(isPlayerPlaying);
        }
    }

    public SimpleExoPlayer getPlayer()
    {
        return this.player;
    }


    public StreamLoadController getStreamLoadController() {
        return streamLoadController;
    }
}