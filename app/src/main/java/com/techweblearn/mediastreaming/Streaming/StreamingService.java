package com.techweblearn.mediastreaming.Streaming;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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
import com.techweblearn.mediastreaming.R;
import com.techweblearn.mediastreaming.UI.HomeActivity;


public class StreamingService extends Service {


    public static final String TAG=StreamingService.class.getSimpleName();

    boolean isNew=false;
    TorrentOptions torrentOptions;
    TorrentStream torrentStream;
    long duration;
    int bitrate;
    long size;
    StreamStatus streamStatus;
    IBinder iBinder=new LocalBinder();
    boolean isStreaming;
    Torrent currentTorrent;
    Handler handler;
    public static final String CHANNEL_ID="streamingnotification";
    Intent intent;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder build;
    long bytes_downloaded=1024;
    int increment=1024*1024;
    StreamStatusExtended streamStatusExtended;
    Runnable runnable;

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
        notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        handler=new Handler();

        streamStatusExtended=new StreamStatusExtended();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_STICKY;
    }



    public void initTorrent(Uri uri) {


        runnable=new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,1000);
                if(currentTorrent.hasBytes(bytes_downloaded)){
                    bytes_downloaded+=increment;
                }
                streamStatusExtended.setDownloadedBytes(bytes_downloaded);

                GlobalEventBus.getBus().post(new Events.StreamStatusExtendedBus(streamStatusExtended));
            }
        };

       handler.postDelayed(runnable,1000);

        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d(TAG, "Prepared");

                GlobalEventBus.getBus().post(new Events.StreamPrepared());
                isStreaming=true;
                currentTorrent=torrent;
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

                currentTorrent=torrent;

                createNotification();


            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG, "Error");

                e.printStackTrace();
            }

            @Override
            public void onStreamReady(Torrent torrent) {

                currentTorrent=torrent;

                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(torrent.getVideoFile().getAbsolutePath());
                duration = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                bitrate = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
                size=torrent.getVideoFile().length();
                String title=torrent.getVideoFile().getName();
                String filepath=torrent.getVideoFile().getAbsolutePath();

                streamStatusExtended.setSize(size);
                streamStatusExtended.setDuration(duration);
                streamStatusExtended.setBitrate(bitrate);



                GlobalEventBus.getBus().post(new Events.StreamStartedBus(new VideoInfo(title,filepath,size,bitrate,duration)));
                GlobalEventBus.getBus().post(new Events.StreamReadyBus(new VideoInfo(title,filepath,size,bitrate,duration)));



            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {


                StreamingService.this.streamStatus=streamStatus;
                currentTorrent=torrent;

                Log.d(TAG, "Buffer Progress : " + streamStatus.bufferProgress);
                Log.d(TAG, "Progress : " + streamStatus.progress);
                Log.d(TAG, "Download Speed : " + streamStatus.downloadSpeed);
                Log.d(TAG, "Seeds : " + streamStatus.seeds);


                updateNotification(streamStatus);
               // GlobalEventBus.getBus().post(new Events.StreamStatusBus(streamStatus));

                streamStatusExtended.setBufferProgress(streamStatus.bufferProgress);
                streamStatusExtended.setDownloadSpeed(streamStatus.downloadSpeed);
                streamStatusExtended.setProgress(streamStatus.progress);
                streamStatusExtended.setSeeds(streamStatus.seeds);




            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG, "Stopped");
                isStreaming=false;
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

    public void setInterestedBytes(long interestedBytes)
    {
        torrentStream.getCurrentTorrent().setInterestedBytes(interestedBytes);
    }

    public void stopStream()
    {
        handler.removeCallbacks(runnable);
        torrentStream.stopStream();
        notificationManager.cancel(30);
    }


    OnStreamStatusListener streamStatusListener;
    public interface OnStreamStatusListener
    {

        void streamStatus(String filename,int bufferProgress,int progress);

    }
    public void setStreamStatusListener(OnStreamStatusListener streamStatusListener)
    {
        this.streamStatusListener=streamStatusListener;
    }


    private void createNotification()
    {

        intent = new Intent(this, HomeActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        build = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Streaming")
                .setContentText("Connecting To Seeds")
                .setProgress(100, 100, true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent)
                .setShowWhen(false);



        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Streaming";
            String description = "Streaming Torrent";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(30,build.build());

    }


    private void updateNotification(StreamStatus streamStatus)
    {

        if(streamStatus.bufferProgress!=100)
        {
            build.setContentText("Buffering : "+streamStatus.bufferProgress+"%")
                    .setProgress(100,streamStatus.bufferProgress,false);
        }
        else
        {
            build.setContentText("Progress : "+(int)streamStatus.progress+"%")
                    .setProgress(100, (int) streamStatus.progress,false);
        }

        notificationManager.notify(30,build.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().getSharedPreferences("stream", Context.MODE_PRIVATE).edit().clear().apply();
        notificationManager.cancel(30);
        Log.d(TAG, "onDestroy: Called");
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        getApplicationContext().getSharedPreferences("stream", Context.MODE_PRIVATE).edit().clear().apply();
        notificationManager.cancel(30);
        Log.d(TAG, "onTaskRemoved: Called");
    }

}
