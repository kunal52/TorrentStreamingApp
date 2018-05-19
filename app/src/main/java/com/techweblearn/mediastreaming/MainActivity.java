package com.techweblearn.mediastreaming;


import android.Manifest;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;

import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class MainActivity extends AppCompatActivity {


    public static final String TAG=MainActivity.class.getSimpleName();

    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;


    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private File file;
    private ServerSocket socket;
    private int port=6666;

    private SurfaceView mSurfaceView;
    private SimpleExoPlayer mPlayer;
    private AspectRatioFrameLayout mAspectRatioLayout;

    private PlaybackControlView mPlaybackControlView;
    ExoPlayer exoPlayer;
    DefaultTimeBar defaultTimeBar;

    int connectedclient=0;
    byte[]buffer=new byte[1024];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultTimeBar=findViewById(R.id.exo_progress);

















        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "MediaStreaming"), (TransferListener<? super DataSource>) bandwidthMeter);
        window = new Timeline.Window();






        stream();

    }








    class FileServer extends NanoHTTPD  {

        InputStream inputStream=null;
        long totalsize=-1;
        File file;

        FileServer(int port) {
            super(port);
        }


        FileServer(int port,InputStream inputStream)
        {
            super(port);
            this.inputStream=inputStream;
        }

        FileServer(int port,InputStream inputStream,long totalSize)
        {
            super(port);
            this.inputStream=inputStream;
            this.totalsize=totalSize;
        }

        FileServer(int port,File file)
        {
            super(port);
            this.file=file;
        }

        @Override
        public Response serve(IHTTPSession session) {


            Map<String,String>headers=session.getHeaders();


            for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
                Log.d("HEADERS", stringStringEntry.toString());
            }

           //  file=new File("/storage/emulated/0/Download/Test/Test.mp4");
             FileInputStream fileInputStream = null;
            try {
                 fileInputStream=new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }



            return newChunkedResponse(Response.Status.OK,"video/mp4",fileInputStream);
        }
    }

























    void stream()
    {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                20);


        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        TorrentStream torrentStream = TorrentStream.init(torrentOptions);
        torrentStream.addListener(new TorrentListener() {
            boolean isNotReady=true;
            @Override
            public void onStreamPrepared(Torrent torrent) {
                               Log.d(TAG,"Prepared");
                for(String files:torrent.getFileNames())
                {

                    Log.d("Files",files);

                }

                Log.d("Save Location:: " ,torrent.getSaveLocation().getAbsolutePath()+" :\n  "+torrent.getVideoFile().getAbsolutePath());

            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                Log.d(TAG,"Started");
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG,"Error");
            }

            @Override
            public void onStreamReady(Torrent torrent) {

                Log.d(TAG,"Ready");
                MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(torrent.getSaveLocation().getAbsolutePath());
                String duration=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String bitrate=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);

                Log.d("DURATION",duration);
                Log.d("BITRATE",bitrate);


                prepareExoPlayerFromURL(Uri.parse(torrent.getVideoFile().getAbsolutePath()));



            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {

                Log.d(TAG,"Buffer Progress : "+streamStatus.bufferProgress);
                Log.d(TAG,"Progress : "+streamStatus.progress);
                Log.d(TAG,"Download Speed : "+streamStatus.downloadSpeed);
                Log.d(TAG,"Seeds : "+streamStatus.seeds);



            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG,"Stopped");
            }
        });



        torrentStream.startStream("magnet:?xt=urn:btih:592b32cf0a987f3b9916ae66df24988ddcf01cd8&dn=Den+of+Thieves+2018+UNRATED+720p+BluRay+HEVC+x265-RMTeam+&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969");
    }



    private void prepareExoPlayerFromURL(Uri uri){

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();

        DefaultLoadControl defaultLoadControl=new DefaultLoadControl();
        Handler handler = new Handler();
        ExtractorsFactory extractor = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("ExoPlayer Demo");

        mPlayer = ExoPlayerFactory.newSimpleInstance(
                this,
                new DefaultTrackSelector(new DefaultBandwidthMeter())

        );
        simpleExoPlayerView.setPlayer(mPlayer);

        DataSource.Factory data=new DefaultDataSourceFactory(getApplicationContext(),"MediaStreaming");
        ExtractorMediaSource.Factory vidoSource = new ExtractorMediaSource.Factory(data);
        MediaSource mediaSource=vidoSource.createMediaSource(uri,null,null);

        mPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                Log.d(TAG,timeline.toString());
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d(TAG,error.toString());
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
        });
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);


        defaultTimeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                Log.d("SCRUB1", String.valueOf(position));
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                Log.d("SCRUB2", String.valueOf(position));
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                Log.d("SCRUB3", String.valueOf(position));
            }
        });


    }





    private void initplayer()
    {


        Handler handler = new Handler();
        ExtractorsFactory extractor = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("ExoPlayer Demo");

        mPlayer = ExoPlayerFactory.newSimpleInstance(
                this,
                new DefaultTrackSelector(new DefaultBandwidthMeter()),
                new DefaultLoadControl()
        );
        mPlaybackControlView = (PlaybackControlView) findViewById(R.id.player_view);
        mPlaybackControlView.requestFocus();
        mPlaybackControlView.setPlayer(mPlayer);

        // initialize source
        MediaSource videoSource = new ExtractorMediaSource(
                Uri.parse("http://127.0.0.1:8080"),
                dataSourceFactory,
                extractor,
                null,
                null
        );
        mPlayer.prepare(videoSource);
        mPlayer.setPlayWhenReady(true);


    }


    private void initPlayer2()
    {
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();

        Handler handler = new Handler();
        ExtractorsFactory extractor = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("ExoPlayer Demo");

        mPlayer = ExoPlayerFactory.newSimpleInstance(
                this,
                new DefaultTrackSelector(new DefaultBandwidthMeter())

        );
        simpleExoPlayerView.setPlayer(mPlayer);

        DataSource.Factory data=new DefaultDataSourceFactory(getApplicationContext(),"MediaStreaming");
         ExtractorMediaSource.Factory vidoSource = new ExtractorMediaSource.Factory(data);
        MediaSource mediaSource=vidoSource.createMediaSource(Uri.parse("http://localhost:8080"),null,null);

        mPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                Log.d(TAG,timeline.toString());
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d(TAG,error.toString());
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
        });
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);


    }


    private void initializePlayer() {

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);



        simpleExoPlayerView.setPlayer(player);


        player.setPlayWhenReady(true);
        MediaSource mediaSource = new HlsMediaSource(Uri.parse("http://localhost:8080"),
                mediaDataSourceFactory, null, null);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


     //   MediaSource mediaSource = new ExtractorMediaSource(Uri.parse("http://127.0.0.1:8080"),
      //          mediaDataSourceFactory, extractorsFactory, null, null);



        player.prepare(mediaSource);
/*
        ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleExoPlayerView.hideController();
            }
        });*/
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }


}
