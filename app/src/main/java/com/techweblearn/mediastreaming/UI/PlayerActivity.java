package com.techweblearn.mediastreaming.UI;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.Models.VideoInfo;
import com.techweblearn.mediastreaming.Playback.ExoPlayerVideoHandler;
import com.techweblearn.mediastreaming.Models.PlayerInfo;
import com.techweblearn.mediastreaming.Playback.StreamLoadController;
import com.techweblearn.mediastreaming.R;
import com.techweblearn.mediastreaming.Streaming.StreamStatusExtended;

import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

public class PlayerActivity extends AppCompatActivity implements SimpleExoPlayer.EventListener, SeekBar.OnSeekBarChangeListener {

    public static final String TAG=PlayerActivity.class.getSimpleName();

    SimpleExoPlayer player;
    PlayerView playerView;
    Uri playuri= Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Test.mp4");
    DefaultTimeBar timeBar;
    AppCompatSeekBar seekBar;
    Handler handler;
    int playBackState=Player.STATE_IDLE;
    int durationSec;
    long buffered;
    boolean isDragging=false;
    StreamStatusExtended streamStatusExtended;
    StreamLoadController streamLoadController;
    VideoInfo videoInfo;
    PlayerInfo playerInfo;


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        GlobalEventBus.getBus().register(this);
        seekBar=findViewById(R.id.exo_progress_custom);
        seekBar.setOnSeekBarChangeListener(this);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},9);
        handler=new Handler();

        playerView=findViewById(R.id.player_view);
        playerInfo=new PlayerInfo();


        if(getIntent()!=null) {
            videoInfo=getIntent().getParcelableExtra("videoinfo");
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
        ExoPlayerVideoHandler.getInstance().goToForeground();
    }


    @Override
    protected void onPause() {
        super.onPause();
        ExoPlayerVideoHandler.getInstance().goToBackground();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        hideStatusBar();

        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalEventBus.getBus().unregister(this);

    }

    private void initPlayer(Uri fileUri)
    {

        ExoPlayerVideoHandler.getInstance().prepareExoPlayerForUri(this,fileUri,playerView);
        ExoPlayerVideoHandler.getInstance().addListener(this);
        player=ExoPlayerVideoHandler.getInstance().getPlayer();
        ExoPlayerVideoHandler.getInstance().getStreamLoadController().setVideoInfo(videoInfo);
        initProgressBar();
    }


    private void hideStatusBar()
    {
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        playBackState=playbackState;

        if(playbackState==Player.STATE_BUFFERING)
            Toast.makeText(this,"Buffering",Toast.LENGTH_LONG).show();

        if(playbackState==Player.STATE_READY)
            Toast.makeText(this,"Ready",Toast.LENGTH_LONG).show();




    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {



    }


    //Seekbar

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser)
        {

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        player.setPlayWhenReady(false);
        isDragging=true;

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        player.setPlayWhenReady(true);
        player.seekTo(seekBar.getProgress()*1000);
        isDragging=false;
    }



    private void updateProgressBar(Runnable runnable) {

        playerInfo.setBufferedPercentage(player.getBufferedPercentage());
        playerInfo.setBufferedProgress(player.getBufferedPosition());
        playerInfo.setCurrentProgress(player.getContentPosition());

        ExoPlayerVideoHandler.getInstance().getStreamLoadController().setPlayerInfo(playerInfo);

        Log.d(TAG, String.valueOf(player.getDuration()));
        Log.d(TAG, String.valueOf(player.getCurrentPosition()));
        Log.d(TAG, String.valueOf(player.getContentPosition()));
        seekBar.setMax(progressBarValueInSec(player.getDuration()));

        if(!isDragging)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            seekBar.setProgress(progressBarValueInSec(player.getCurrentPosition()),true);
        else
            seekBar.setProgress(progressBarValueInSec(player.getCurrentPosition()));
        handler.postDelayed(runnable,1000);
    }


    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgressBar(this);
        }
    };



    private void initProgressBar()
    {

        seekBar.setMax(progressBarValueInSec(player.getDuration()));
        handler.post(updateProgressAction);

    }


    int progressBarValueInSec(long ms)
    {
        return (int) (ms/1000);
    }




    @Subscribe
   public void StreamReady(Events.StreamReadyBus streamReadyBus)
    {
        initPlayer(Uri.parse(videoInfo.getFilePath()));

    }

    @Subscribe
    public void StreamStatus(Events.StreamStatusBus streamStatusBus)
    {

        if(streamStatusBus.getStreamStatus().progress==100)
        {
            initPlayer(Uri.parse(videoInfo.getFilePath()));
        }
        try {


        ExoPlayerVideoHandler.getInstance().getStreamLoadController().setStreamStatus(streamStatusBus.getStreamStatus());
        }catch (Exception e){}
    }

}
