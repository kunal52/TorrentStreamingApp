package com.techweblearn.mediastreaming.UI;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.R;
import com.techweblearn.mediastreaming.Streaming.StreamingService;
import com.techweblearn.mediastreaming.adapter.TorrentFilesListAdapter;
import com.techweblearn.mediastreaming.mvp.classes.HomeActivityPresenter;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityPresenterInter;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityViewInter;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeActivity extends AppCompatActivity implements HomeActivityViewInter,TorrentFilesListAdapter.ItemClickCallback {

    @BindView(R.id.torrent_file_list_recyclerview)RecyclerView torrentFileList;

    public static final String TAG_URI = "uri";
    public static final String TAG = HomeActivity.class.getSimpleName();
    private Torrent torrent;
    private StreamStatus streamStatus;
    Uri playuri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test.mp4");
    Uri uri;
    StreamingService streamingService;
    boolean mBounded;
    HomeActivityPresenterInter homeActivityPresenterInter;
    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},9);
        unbinder=ButterKnife.bind(this);


        homeActivityPresenterInter=new HomeActivityPresenter(this);
        homeActivityPresenterInter.searchTorrentFiles();

        GlobalEventBus.getBus().register(this);

        Intent intent = getIntent();
        if (intent.getData() != null) {
            uri = intent.getData();
        } else {
            uri = intent.getParcelableExtra(TAG_URI);
        }

        if (uri != null) {
           startService(uri);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalEventBus.getBus().unregister(this);
        unbinder.unbind();

    }



    @Subscribe
    public void getStreamStarted(Events.StreamStartedBus streamStartedBus)
    {
        Intent intent=new Intent(HomeActivity.this,PlayerActivity.class);
        intent.putExtra("videoinfo",streamStartedBus.getVideoInfo());
        startActivity(intent);
        finish();
    }

    @Override
    public void onCompleted(ArrayList<File> files) {
        for (File file:files)
        {
            Log.d(TAG,file.getName());
        }

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


    @Override
    public void onClick(File file) {

        startService(Uri.fromFile(file));
    }


    private void startService(Uri uri)
    {
        Log.d("URI", uri.toString());
        final Intent intent1 = new Intent(this, StreamingService.class);
        intent1.putExtra("uri", uri.toString());
        try {
            stopService(intent1);
        }catch (Exception e){e.printStackTrace();}

        bindService(intent1, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("SERVICE","Connected");
                mBounded=true;
                StreamingService.LocalBinder mLocalBinder = (StreamingService.LocalBinder)service;
                streamingService = mLocalBinder.getServerInstance();
                streamingService.initTorrent(intent1);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("SERVICE","DisConnected");
            }
        },BIND_AUTO_CREATE);
        // TorrentProcessingDialogFragment.getInstance().show(getSupportFragmentManager(),"");
        Log.d(TAG,"Torrent");
    }

}
