package com.techweblearn.mediastreaming.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.frostwire.jlibtorrent.LibTorrent;
import com.frostwire.jlibtorrent.TorrentBuilder;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.Playback.StreamLoadController;
import com.techweblearn.mediastreaming.R;
import com.techweblearn.mediastreaming.Singleton.TorrentInfoSigleton;
import com.techweblearn.mediastreaming.Streaming.StreamStatusExtended;
import com.techweblearn.mediastreaming.Streaming.StreamingAsync;
import com.techweblearn.mediastreaming.Streaming.StreamingService;

import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {


    public static final String TAG_URI = "uri";
    public static final String TAG = HomeActivity.class.getSimpleName();
    private Torrent torrent;
    private StreamStatus streamStatus;
    Uri playuri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test.mp4");
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GlobalEventBus.getBus().register(this);



        Intent intent = getIntent();
        if (intent.getData() != null) {
            uri = intent.getData();
        } else {
            uri = intent.getParcelableExtra(TAG_URI);
        }

        if (uri != null) {
            Log.d("URI", uri.toString());
            Intent intent1 = new Intent(this, StreamingService.class);
            intent1.putExtra("uri", uri.toString());

            startService(intent1);

        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalEventBus.getBus().unregister(this);
    }


    @Subscribe
    public void getStreamStarted(Events.StreamStartedBus streamStartedBus)
    {
        Intent intent=new Intent(HomeActivity.this,PlayerActivity.class);
        intent.putExtra("videoinfo",streamStartedBus.getVideoInfo());
        startActivity(intent);
        finish();
    }
}