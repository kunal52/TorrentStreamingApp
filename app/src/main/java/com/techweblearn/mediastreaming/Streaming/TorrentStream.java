package com.techweblearn.mediastreaming.Streaming;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.techweblearn.mediastreaming.Singleton.TorrentInfoSigleton;

public class TorrentStream {

    public static final String TAG=TorrentStream.class.getSimpleName();

    public void loadTorrent(Uri uri)
    {
        final TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        final com.github.se_bastiaan.torrentstream.TorrentStream torrentStream = com.github.se_bastiaan.torrentstream.TorrentStream.init(torrentOptions);

        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d(TAG,"Prepared");
                for(String files:torrent.getFileNames())
                {

                    Log.d("Files",files);

                }

                TorrentInfoSigleton.getInstance().setTitle(torrent.getSaveLocation().getName());

                listener.onStreamPrepared(torrent);

            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                Log.d(TAG,"Started");
                listener.onStreamStarted(torrent);

            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG,"Error");
                listener.onStreamError(torrent, e);
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

                TorrentInfoSigleton.getInstance().setBitrate(Long.parseLong(bitrate));
                // prepareExoPlayerFromURL(Uri.parse(torrent.getVideoFile().getAbsolutePath()));

                listener.onStreamReady(torrent);

            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {



                Log.d(TAG,"Buffer Progress : "+streamStatus.bufferProgress);
                Log.d(TAG,"Progress : "+streamStatus.progress);
                Log.d(TAG,"Download Speed : "+streamStatus.downloadSpeed);
                Log.d(TAG,"Seeds : "+streamStatus.seeds);
                listener.onStreamProgress(torrent, streamStatus);


            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG,"Stopped");
                listener.onStreamStopped();
            }
        });

        if(uri!=null) {
            Log.d("URI", uri.getScheme());
            Log.d("URI",uri.getQuery());
            Log.d("URI",uri.getEncodedQuery());
        }

        if(uri!=null)
            torrentStream.startStream(uri.toString());
    }


    private TorrentInfoListener listener;

    public interface TorrentInfoListener
    {

        void onStreamPrepared(Torrent torrent);

        void onStreamStarted(Torrent torrent);

        void onStreamError(Torrent torrent, Exception e);

        void onStreamReady(Torrent torrent);

        void onStreamProgress(Torrent torrent, StreamStatus streamStatus);

        void onStreamStopped();

    }

    public void setTorrentInfoListener(TorrentInfoListener listener)
    {
        this.listener=listener;
    }


}
