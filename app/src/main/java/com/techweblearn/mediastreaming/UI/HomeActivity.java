package com.techweblearn.mediastreaming.UI;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.Models.VideoInfo;
import com.techweblearn.mediastreaming.R;
import com.techweblearn.mediastreaming.Streaming.StreamStatusExtended;
import com.techweblearn.mediastreaming.Streaming.StreamingService;
import com.techweblearn.mediastreaming.adapter.TorrentFilesListAdapter;
import com.techweblearn.mediastreaming.dialogs.TorrentProcessingDialogFragment;
import com.techweblearn.mediastreaming.mvp.classes.HomeActivityPresenter;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityPresenterInter;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityViewInter;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeActivity extends AppCompatActivity implements HomeActivityViewInter
        ,TorrentFilesListAdapter.ItemClickCallback
        ,DialogInterface.OnDismissListener
        ,View.OnClickListener{

    @BindView(R.id.torrent_file_list_recyclerview)RecyclerView torrentFileList;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.movie_title_textview)TextView movie_title_textview;
    @BindView(R.id.play_imagebutton)ImageButton play_imagebutton;
    @BindView(R.id.stop_imagebutton)ImageButton stop_imagebutton;
    @BindView(R.id.status_textview)TextView status_textview;
    @BindView(R.id.status_percentage_textview)TextView status_percentaage_textview;
    @BindView(R.id.bottom_play_layout_noplay)RelativeLayout bottom_play_layout;

    public static final String TAG_URI = "uri";
    public static final String TAG = HomeActivity.class.getSimpleName();
    private Torrent torrent;
    private StreamStatus streamStatus;
    private VideoInfo videoinfo;

    Uri playuri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test.mp4");
    Uri uri;
    StreamingService streamingService;
    boolean mBounded;
    HomeActivityPresenterInter homeActivityPresenterInter;
    Unbinder unbinder;
    boolean streamReady;
    boolean streamPrepared;
    TorrentProcessingDialogFragment torrentProcessingDialogFragment;
    File playing_torrent_file;
    boolean isStreaming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},9);
        unbinder=ButterKnife.bind(this);
        homeActivityPresenterInter=new HomeActivityPresenter(getContentResolver(),this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Torrents");


        Intent intent = getIntent();
        if (intent.getData() != null) {
            uri = intent.getData();
        } else {
            uri = intent.getParcelableExtra(TAG_URI);
        }

        if (uri != null) {

           homeActivityPresenterInter.getTorrentFile(uri);
        }
        else homeActivityPresenterInter.searchTorrentFiles();

        play_imagebutton.setOnClickListener(this);
        stop_imagebutton.setOnClickListener(this);

        Intent intent1 = new Intent(getApplication(), StreamingService.class);
        bindService(intent1,serviceConnection,BIND_AUTO_CREATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalEventBus.getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if(mBounded)
        unbindService(serviceConnection);

    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalEventBus.getBus().unregister(this);
    }

    //Torrent File Search Completed into Storage
    @Override
    public void onCompleted(ArrayList<File> files) {


        TorrentFilesListAdapter torrentFilesListAdapter=new TorrentFilesListAdapter(this,files);
        torrentFilesListAdapter.setItemClickCallback(this);
        torrentFileList.setLayoutManager(new LinearLayoutManager(this));
        torrentFileList.setAdapter(torrentFilesListAdapter);


    }

    @Override
    public void onError() {



    }

    @Override
    public void onPermissionError() {

    }


    //Get Torrent File
    @Override
    public void onCompleted(File file) {
        startService(Uri.fromFile(file));
        Log.d(TAG,"Torrent File Completed");
        playing_torrent_file=file;
        torrentProcessingDialogFragment.dismiss();
    }

    @Override
    public void onError(String message) {

    }


    //Grt File When Click
    @Override
    public void onClick(File file) {

        playing_torrent_file=file;
        startService(Uri.fromFile(file));
    }


    ServiceConnection serviceConnection=new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("SERVICE","Connected");
            StreamingService.LocalBinder mLocalBinder = (StreamingService.LocalBinder)service;
            streamingService = mLocalBinder.getServerInstance();
            mBounded=true;
            streamingService.setStreamStatusListener(new StreamingService.OnStreamStatusListener() {
                @Override
                public void getStreamStatus(String name, StreamStatus streamStatus) {
                    Log.d("STREAMING STATUS",name);
                    movie_title_textview.setText(name);
                    updateUI(streamStatus);

                }
            });
            streamingService.getStatus();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("SERVICE","DisConnected");

        }
    };

    private void startService(Uri uri)
    {

        if(!isStreaming) {

            this.uri = uri;
            isStreaming = true;
            if (mBounded)
                streamingService.initTorrent(uri);
            Log.d(TAG, "Torrent");

            torrentProcessingDialogFragment = TorrentProcessingDialogFragment.getInstance();
            torrentProcessingDialogFragment.show(getSupportFragmentManager(), "");
        }else
        {
            Toast.makeText(this, "Cancel Previous Stream before Play Another", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        if(!streamPrepared) {
            if (streamingService != null)
                streamingService.stopStream();
            unbindService(serviceConnection);
            Log.d("DIALOG","DISMISS");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.play_imagebutton:
                playVideo();
                break;

            case R.id.stop_imagebutton:

                streamingService.stopStream();
                bottom_play_layout.setVisibility(View.VISIBLE);
                isStreaming=false;

                break;

        }


    }


    private void playVideo()
    {
        StreamStatusExtended streamStatusExtended=new StreamStatusExtended(
                streamStatus.progress,
                streamStatus.bufferProgress,
                streamStatus.seeds,
                streamStatus.downloadSpeed,
                0,0,0);

        Intent intent=new Intent(this,PlayerActivity.class);
        intent.putExtra("stream_status",streamStatusExtended);
        intent.putExtra("videoinfo",videoinfo);
        startActivity(intent);
    }



    @Subscribe
    public void getStreamStarted(Events.StreamStartedBus streamStartedBus)
    {
       /* Intent intent=new Intent(HomeActivity.this,PlayerActivity.class);
        intent.putExtra("videoinfo",streamStartedBus.getVideoInfo());
        startActivity(intent);*/
        streamReady=true;
        videoinfo=streamStartedBus.getVideoInfo();
        play_imagebutton.setClickable(true);
        play_imagebutton.setVisibility(View.VISIBLE);

        //finish();
    }

    @Subscribe
    public void getStreamPrepared(Events.StreamPrepared streamPrepared)
    {
       /* Intent intent=new Intent(HomeActivity.this,TorrentStatusActivity.class);
        //intent.putExtra("videoinfo",streamStartedBus.getVideoInfo());
        startActivity(intent);
        finish();*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bottom_play_layout.setVisibility(View.GONE);
                HomeActivity.this.streamPrepared=true;
                torrentProcessingDialogFragment.dismiss();
                movie_title_textview.setText(playing_torrent_file.getName());
            }
        },2000);

    }

    @Subscribe
    public void getStreamStatus(Events.StreamStatusBus streamStatusBus)
    {
        streamStatus=streamStatusBus.getStreamStatus();
        updateUI(streamStatus);

    }


    private void updateUI(StreamStatus streamStatus)
    {
        if(bottom_play_layout.getVisibility()==View.VISIBLE)
            bottom_play_layout.setVisibility(View.GONE);
        if(streamStatus.bufferProgress!=100) {
            status_percentaage_textview.setText(String.valueOf(streamStatus.bufferProgress));
            status_textview.setText("Buffering");
        }
        else
        {
            status_percentaage_textview.setText(String.valueOf(streamStatus.progress));
            status_textview.setText("Progress");
        }
    }


}
