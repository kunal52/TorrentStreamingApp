package com.techweblearn.mediastreaming.Playback;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;

public class ExoPlayerVideoHandler
{
    public static final int RENDERER_COUNT = 4;
    public static final int DEFAULT_MIN_BUFFER_MS = 1000;
    public static final int DEFAULT_MIN_REBUFFER_MS = 5000;
    private static ExoPlayerVideoHandler instance;
    private DataSource.Factory dataSourFactory;
    private MediaSource videoMediaSource;

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
                streamLoadController=new StreamLoadController();

                player= ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(context),new DefaultTrackSelector(),streamLoadController);

                dataSourFactory=new DefaultDataSourceFactory(context,"TorrentStreaming");
                ExtractorMediaSource.Factory vidoSource = new ExtractorMediaSource.Factory(dataSourFactory);
                videoMediaSource=vidoSource.createMediaSource(playerUri,null,null);
             //   player.prepare(addSubtitle(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/Test.srt"))));
                player.prepare(videoMediaSource);

            }
            player.clearVideoSurface();
            player.setVideoSurfaceView(
                    (SurfaceView)exoPlayerView.getVideoSurfaceView());
            player.seekTo(player.getCurrentPosition() + 1);
            exoPlayerView.setPlayer(player);
            player.setPlayWhenReady(true);

        }
    }

    private MediaSource addSubtitle(Uri subtitleUri)
    {
        Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
                null, Format.NO_VALUE, Format.NO_VALUE, "en", null, Format.OFFSET_SAMPLE_RELATIVE);

        MediaSource subtitleSource=new SingleSampleMediaSource.Factory(dataSourFactory).createMediaSource(subtitleUri
                , textFormat,C.TIME_UNSET);

        return new MergingMediaSource(videoMediaSource,subtitleSource);


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