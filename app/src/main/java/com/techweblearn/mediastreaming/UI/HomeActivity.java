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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
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
import java.util.Locale;
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
    @BindView(R.id.progressBar)ProgressBar contentLoadingrogressBar;
    @BindView(R.id.no_torrent_file_text)TextView no_torrent_file_text;

    public static final String TAG_URI = "uri";
    public static final String TAG = HomeActivity.class.getSimpleName();
    private Torrent torrent;
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
    boolean isStreamReady;
    StreamStatusExtended streamStatusExtended;
    boolean close;

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
        homeActivityPresenterInter.searchTorrentFiles();

        contentLoadingrogressBar.setVisibility(View.VISIBLE);

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
    protected void onSaveInstanceState(Bundle outState) {
        if(outState!=null) {
            outState.putBoolean("isready", isStreamReady);
            outState.putBoolean("isstreaming", isStreaming);
            outState.putParcelable("videoinfo", videoinfo);
            outState.putParcelable("streamstatusextended", streamStatusExtended);
            outState.putString("videoname", movie_title_textview.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        isStreamReady=savedInstanceState.getBoolean("isready",false);
        isStreaming=savedInstanceState.getBoolean("isstreaming",false);
        videoinfo=savedInstanceState.getParcelable("videoinfo");
        streamStatusExtended=savedInstanceState.getParcelable("streamingstatusextended");


        if(isStreaming) {

            if(isStreamReady) {
                status_textview.setText(getString(R.string.progress));
              //  status_percentaage_textview.setText(String.valueOf(String.format(Locale.US,"%.2f",streamStatusExtended.getProgress()) +"%"));
                play_imagebutton.setVisibility(View.VISIBLE);
            }
            else
            {
                status_textview.setText(getString(R.string.buffering));
              //  status_percentaage_textview.setText(String.valueOf(streamStatusExtended.getBufferProgress()+getString(R.string.percent_sign)));
            }


            movie_title_textview.setText(savedInstanceState.getString("videoname"));

        }



        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalEventBus.getBus().unregister(this);
        Log.d(TAG, "onStop: ");
    }

    //Torrent File Search Completed into Storage
    @Override
    public void onCompleted(ArrayList<File> files) {

        if(files==null||files.size() == 0)
        {
            no_torrent_file_text.setVisibility(View.VISIBLE);
            return;
        }

        TorrentFilesListAdapter torrentFilesListAdapter=new TorrentFilesListAdapter(this,files);
        torrentFilesListAdapter.setItemClickCallback(this);
        torrentFileList.setLayoutManager(new LinearLayoutManager(this));
        torrentFileList.setAdapter(torrentFilesListAdapter);
        contentLoadingrogressBar.setVisibility(View.GONE);


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

        //TODO Remove Test Torrent
        playing_torrent_file=file;
        startService(Uri.fromFile(file));
       // testTorrent(Uri.fromFile(file));
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
                public void streamStatus(String filename, int bufferProgress, int progress) {
                    bottom_play_layout.setVisibility(View.GONE);
                    movie_title_textview.setText(filename);
                    if(bufferProgress!=100|| progress < 2) {
                        status_percentaage_textview.setText(String.valueOf(bufferProgress +"%"));
                        status_textview.setText("Buffering ");
                    }
                    else
                    {
                        status_percentaage_textview.setText(String.valueOf(progress +"%"));
                        status_textview.setText("Progress");
                        play_imagebutton.setVisibility(View.VISIBLE);
                    }

                }
            });

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
            if (streamingService != null) {
                streamingService.stopStream();
                isStreaming=false;
                bottom_play_layout.setVisibility(View.VISIBLE);
            }

          //  unbindService(serviceConnection);
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

    @Override
    public void onBackPressed() {


       // CloseAppDialog.getInstance().show(getSupportFragmentManager(),"close");
        super.onBackPressed();
    }

    private void playVideo()
    {
        /*StreamStatusExtended streamStatusExtended=new StreamStatusExtended(
                HomeActivity.this.streamStatusExtended.getProgress(),
                HomeActivity.this.streamStatusExtended.getBufferProgress(),
                HomeActivity.this.streamStatusExtended.getSeeds(),
                HomeActivity.this.streamStatusExtended.getDownloadSpeed(),
                0,0,0,HomeActivity.this.streamStatusExtended.getDownloadedBytes());*/

        Intent intent=new Intent(this,PlayerActivity.class);
        intent.putExtra("stream_status",streamStatusExtended);
        intent.putExtra("videoinfo",videoinfo);
        startActivity(intent);
    }


    @Subscribe
    public void onStreamReadyBus(Events.StreamReadyBus streamReadyBus)
    {
        videoinfo=streamReadyBus.getVideoInfo();
        isStreamReady=true;
        play_imagebutton.setClickable(true);
        play_imagebutton.setVisibility(View.VISIBLE);

    }

    @Subscribe
    public void onStreamPrepared(Events.StreamPrepared streamPrepared)
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
      //  streamStatus=streamStatusBus.getStreamStatus();
        //updateUI(streamStatus);

    }


    @Subscribe
    public void getExtendedStreamStatus(Events.StreamStatusExtendedBus streamStatusExtendedBus)
    {

        Log.d(TAG, "getExtendedStreamStatus: "+streamStatusExtendedBus.getStreamStatusExtended().toString());
        HomeActivity.this.streamStatusExtended=streamStatusExtendedBus.getStreamStatusExtended();

        if(streamStatusExtended.getBufferProgress()>0)
        updateUI(streamStatusExtendedBus.getStreamStatusExtended());

    }


    @Subscribe
    public void onStreamReady(Events.StreamReadyBus streamReadyBus)
    {
        Log.d(TAG, "onStreamReady: ");
        this.isStreamReady=true;
        videoinfo=streamReadyBus.getVideoInfo();
        status_textview.setText("Progress");
        play_imagebutton.setVisibility(View.VISIBLE);
    }

    private void updateUI(StreamStatusExtended streamStatusExtended)
    {
        if(bottom_play_layout.getVisibility()==View.VISIBLE)
            bottom_play_layout.setVisibility(View.GONE);

        if(streamReady||streamStatusExtended.getBufferProgress()==100) {
            
            status_textview.setText(getString(R.string.progress));
            status_percentaage_textview.setText(String.format(Locale.US,"%.2f",streamStatusExtended.getProgress())+getString(R.string.percent_sign));
        }
        else {
            status_textview.setText(getString(R.string.buffering));
            status_percentaage_textview.setText(String.valueOf(streamStatusExtended.getBufferProgress() + getString(R.string.percent_sign)));
        }

    }


    private void initUI()
    {

    }

    private void testTorrent(final Uri uri)
    {
        TorrentOptions torrentOptions=new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        TorrentStream torrentStream=TorrentStream.init(torrentOptions);
        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d(TAG, "onStreamPrepared: ");
            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                Log.d(TAG, "onStreamStarted: ");
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG, "onStreamError: ");
                e.printStackTrace();
            }

            @Override
            public void onStreamReady(Torrent torrent) {
                Log.d(TAG, "onStreamReady: ");
                Log.d(TAG, "onStreamReady: "+torrent.getSaveLocation().getAbsolutePath());
                Intent intent=new Intent(HomeActivity.this,PlayerActivity.class);
                intent.putExtra("uri",torrent.getVideoFile().getAbsolutePath());
                startActivity(intent);

            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {
                Log.d(TAG, "onStreamProgress: ");
                Log.d(TAG, "onStreamProgress: "+streamStatus.bufferProgress);
                Log.d(TAG, "onStreamProgress: "+streamStatus.progress);
            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG, "onStreamStopped: ");
            }
        });
        torrentStream.startStream(uri.toString());
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }
}
