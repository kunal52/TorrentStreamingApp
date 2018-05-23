package com.techweblearn.mediastreaming.Streaming;

import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.frostwire.jlibtorrent.TorrentInfo;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.exceptions.TorrentInfoException;
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
    IBinder iBinder=new LocalBinder();


    public class LocalBinder extends Binder {
        public StreamingService getServerInstance() {
            return StreamingService.this;
        }
    }


    public StreamingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
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



        return START_STICKY;
    }



    public void initTorrent(Intent intent) {

        Uri uri= Uri.parse(intent.getStringExtra("uri"));

        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d(TAG, "Prepared");


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


                }catch (Exception e){
                    isNew=true;
                }

            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG, "Error");

                e.printStackTrace();
            }

            @Override
            public void onStreamReady(Torrent torrent) {

                Log.d(TAG, "Ready");
                Log.d("DURATION", String.valueOf(duration));
                Log.d("BITRATE", String.valueOf(bitrate));

                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(torrent.getVideoFile().getAbsolutePath());
                duration = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                bitrate = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
                size=torrent.getVideoFile().length();
                String title=torrent.getVideoFile().getName();
                String filepath=torrent.getVideoFile().getAbsolutePath();


                GlobalEventBus.getBus().post(new Events.StreamStartedBus(new VideoInfo(title,filepath,size,bitrate,duration)));
                GlobalEventBus.getBus().post(new Events.StreamReadyBus());



            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {


                Log.d(TAG, "Buffer Progress : " + streamStatus.bufferProgress);
                Log.d(TAG, "Progress : " + streamStatus.progress);
                Log.d(TAG, "Download Speed : " + streamStatus.downloadSpeed);
                Log.d(TAG, "Seeds : " + streamStatus.seeds);


                if(streamStatus.bufferProgress==100)

                GlobalEventBus.getBus().post(new Events.StreamStatusBus(streamStatus));

            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG, "Stopped");
            }
        });


        if (uri != null) {

           // torrentStream.startStream("magnet:?xt=urn:btih:592b32cf0a987f3b9916ae66df24988ddcf01cd8&dn=Den+of+Thieves+2018+UNRATED+720p+BluRay+HEVC+x265-RMTeam+&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969");
            Log.d("URI","TORRENT");
            Log.d("URI",uri.toString());
            Log.d("URI",uri.getPath());



            torrentStream.setContentResolver(getContentResolver());
            TorrentInfo torrentInfo;
            try {
                torrentInfo=torrentStream.getTorrentInfo(uri.toString());
                Log.d(TAG, String.valueOf(torrentInfo.numFiles()));


            } catch (TorrentInfoException e) {
                e.printStackTrace();
            }

            torrentStream.startStream(uri.toString());
        }

    }

    public void pauseStream()
    {
        torrentStream.pauseSession();
        Log.d(TAG,"PAUSE");

    }

    public void resumeStream()
    {
        torrentStream.resumeSession();
        Log.d(TAG,"Resume");
    }




}
