package com.techweblearn.mediastreaming.Streaming;

import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.Models.VideoInfo;

public class StreamingService extends Service {


    public static final String TAG=StreamingService.class.getSimpleName();

    boolean isNew=false;
    TorrentOptions torrentOptions;
    TorrentStream torrentStream;
    long duration;
    int bitrate;
    long size;

    public StreamingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        torrentOptions=new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        torrentStream = TorrentStream.init(torrentOptions);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Uri uri= Uri.parse(intent.getStringExtra("uri"));
        initTorrent(uri);

        return START_STICKY;
    }



    public void initTorrent(Uri uri) {


        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d(TAG, "Prepared");

                for (String files : torrent.getFileNames()) {
                    Log.d("Files", files);
                }

                Log.d("Save Location:: ", torrent.getSaveLocation().getAbsolutePath());

            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                Log.d(TAG, "Started");

                try {


                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(torrent.getSaveLocation().getAbsolutePath());
                duration = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                bitrate = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
                size=torrent.getVideoFile().length();
                String title=torrent.getVideoFile().getName();
                String filepath=torrent.getVideoFile().getAbsolutePath();

                streamStarted(new VideoInfo(title,filepath,size,bitrate,duration));
                }catch (Exception e){
                    isNew=true;
                }
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG, "Error");
            }

            @Override
            public void onStreamReady(Torrent torrent) {

                Log.d(TAG, "Ready");
                Log.d("DURATION", String.valueOf(duration));
                Log.d("BITRATE", String.valueOf(bitrate));





                GlobalEventBus.getBus().post(new Events.StreamReadyBus());



            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {


                if(isNew) {
                    String title=torrent.getVideoFile().getName();
                    String filepath=torrent.getVideoFile().getAbsolutePath();
                    streamStarted(new VideoInfo(title, filepath, size, bitrate, duration));
                    isNew=false;
                }

                Log.d(TAG, "Buffer Progress : " + streamStatus.bufferProgress);
                Log.d(TAG, "Progress : " + streamStatus.progress);
                Log.d(TAG, "Download Speed : " + streamStatus.downloadSpeed);
                Log.d(TAG, "Seeds : " + streamStatus.seeds);


                GlobalEventBus.getBus().post(new Events.StreamStatusBus(streamStatus));

            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG, "Stopped");
            }
        });


        if (uri != null) {

            torrentStream.startStream("magnet:?xt=urn:btih:592b32cf0a987f3b9916ae66df24988ddcf01cd8&dn=Den+of+Thieves+2018+UNRATED+720p+BluRay+HEVC+x265-RMTeam+&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969");
            Log.d("URI","TORRENT");

        }

    }




    private void streamStarted(VideoInfo videoInfo)
    {
           GlobalEventBus.getBus().post(new Events.StreamStartedBus(videoInfo));
    }



}
