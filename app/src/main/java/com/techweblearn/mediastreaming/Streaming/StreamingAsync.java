package com.techweblearn.mediastreaming.Streaming;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

public class StreamingAsync extends AsyncTask<Uri,StreamStatus,String> {


    public static final String TAG=StreamingAsync.class.getSimpleName();

    private TorrentOptions torrentOptions;
    private TorrentStream torrentStream;
    private Context context;

    public StreamingAsync(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {

        torrentOptions=new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        torrentStream = TorrentStream.init(torrentOptions);

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
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG, "Error");
            }

            @Override
            public void onStreamReady(Torrent torrent) {

                Log.d(TAG, "Ready");
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(torrent.getSaveLocation().getAbsolutePath());
                String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String bitrate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);

                Log.d("DURATION", duration);
                Log.d("BITRATE", bitrate);

            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {

                publishProgress(streamStatus);

                Log.d(TAG, "Buffer Progress : " + streamStatus.bufferProgress);
                Log.d(TAG, "Progress : " + streamStatus.progress);
                Log.d(TAG, "Download Speed : " + streamStatus.downloadSpeed);
                Log.d(TAG, "Seeds : " + streamStatus.seeds);
             //   sendTorrentInfoToPlayActivity(streamStatus);

            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG, "Stopped");
            }
        });

    }

    @Override
    protected String doInBackground(Uri... uris) {
        if (uris[0] != null) {

            torrentStream.startStream("magnet:?xt=urn:btih:BEE75372B98077BFD4DE8EF03EB33E9289BE5CD8&dn=Avengers+Infinity+War+2018+NEW+PROPER+720p+HD-CAM+X264+HQ-CPG&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80&tr=udp%3A%2F%2Ftracker.istole.it%3A6969&tr=udp%3A%2F%2Fopen.demonii.com%3A1337");
            Log.d("URI","TORRENT");

        }


        return null;
    }

    @Override
    protected void onProgressUpdate(StreamStatus... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }



  /*  private void sendTorrentInfoToPlayActivity(StreamStatus streamStatus)
    {

        Intent intent=new Intent("TorrentProgress");
        StreamStatusExtended streamStatusExtended=new StreamStatusExtended(streamStatus.progress,streamStatus.bufferProgress,streamStatus.seeds,streamStatus.downloadSpeed);
        intent.putExtra("streamstatus",streamStatusExtended);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }*/

}
