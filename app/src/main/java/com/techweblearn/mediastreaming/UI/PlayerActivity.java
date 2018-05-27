package com.techweblearn.mediastreaming.UI;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.Models.PlayerInfo;
import com.techweblearn.mediastreaming.Models.VideoInfo;
import com.techweblearn.mediastreaming.Playback.ExoPlayerVideoHandler;
import com.techweblearn.mediastreaming.R;
import com.techweblearn.mediastreaming.Streaming.StreamStatusExtended;
import com.techweblearn.mediastreaming.Streaming.StreamingService;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PlayerActivity extends AppCompatActivity implements SimpleExoPlayer.EventListener, SeekBar.OnSeekBarChangeListener, PlayerControlView.VisibilityListener {



    public static final String TAG=PlayerActivity.class.getSimpleName();

    @BindView(R.id.title_bar)LinearLayout titlebarlayout;
    @BindView(R.id.movie_title)TextView movie_title;
    @BindView(R.id.buffer_progressbar)ProgressBar progressBar;

    SimpleExoPlayer player;
    PlayerView playerView;
    Uri playuri= Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Test.mp4");
    AppCompatSeekBar seekBar;
    Handler handler;
    int playBackState=Player.STATE_IDLE;
    boolean isDragging=false;

    VideoInfo videoInfo;
    PlayerInfo playerInfo;
    StreamingService streamingService;
    boolean mBounded;
    boolean isbuffering;
    Unbinder unbinder;
    private StreamStatus streamStatus;

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, StreamingService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);


    }

    ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("SERVICE BIND","Connected");
            Toast.makeText(PlayerActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
            mBounded=true;
            StreamingService.LocalBinder mLocalBinder = (StreamingService.LocalBinder)service;
            streamingService = mLocalBinder.getServerInstance();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("SERVICE BIND","DisConnected");
            Toast.makeText(PlayerActivity.this, "Service Dsiconnected", Toast.LENGTH_SHORT).show();

            mBounded=false;
            streamingService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        unbinder=ButterKnife.bind(this);

        GlobalEventBus.getBus().register(this);
        seekBar=findViewById(R.id.exo_progress_custom);
        seekBar.setOnSeekBarChangeListener(this);

         handler=new Handler();

        playerView=findViewById(R.id.player_view);
        playerView.setControllerVisibilityListener(this);
        playerInfo=new PlayerInfo();


        if(getIntent()!=null) {
            videoInfo=getIntent().getParcelableExtra("videoinfo");
            initPlayer(Uri.parse(videoInfo.getFilePath()));
            movie_title.setText(videoInfo.getTitle());

            if(getIntent().getParcelableExtra("streamstatus")!=null)
            {
                StreamStatusExtended streamStatusExtended=getIntent().getParcelableExtra("streamstatus");



            }

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
        ExoPlayerVideoHandler.getInstance().goToForeground();
        if(streamingService!=null)
        streamingService.resumeStream();
    }


    @Override
    protected void onPause() {
        super.onPause();
        ExoPlayerVideoHandler.getInstance().goToBackground();
        if(streamingService!=null)
        streamingService.pauseStream();
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
        unbinder.unbind();
        unbindService(mConnection);
        finish();

    }

    private void initPlayer(Uri fileUri)
    {

        ExoPlayerVideoHandler.getInstance().prepareExoPlayerForUri(this,fileUri,playerView);
        ExoPlayerVideoHandler.getInstance().addListener(this);
        player=ExoPlayerVideoHandler.getInstance().getPlayer();
        ExoPlayerVideoHandler.getInstance().getStreamLoadController().setVideoInfo(videoInfo);
        ExoPlayerVideoHandler.getInstance().getStreamLoadController().setPlayerInfo(playerInfo);
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

        if(playbackState==Player.STATE_BUFFERING) {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Buffering", Toast.LENGTH_LONG).show();
        }

        if(playbackState==Player.STATE_READY) {
            Toast.makeText(this, "Ready", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }




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
      //  player.setPlayWhenReady(true);
      //  player.seekTo(seekBar.getProgress()*1000);

        streamingService.setInterestedBytes(videoInfo.getTotalSize());
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

        seekBar.setSecondaryProgress(progressBarValueInSec(player.getBufferedPosition()));
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



    }

    @Subscribe
    public void StreamStatus(Events.StreamStatusBus streamStatusBus)
    {

        try {
        ExoPlayerVideoHandler.getInstance().getStreamLoadController().setStreamStatus(streamStatusBus.getStreamStatus());
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onVisibilityChange(int visibility) {

        titlebarlayout.setVisibility(visibility);

    }
}
