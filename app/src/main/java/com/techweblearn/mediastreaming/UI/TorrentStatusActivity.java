package com.techweblearn.mediastreaming.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.R;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TorrentStatusActivity extends AppCompatActivity {

    @BindView(R.id.status_textview)TextView status_textview;
    @BindView(R.id.download_speed_textview)TextView download_speed_textview;
    @BindView(R.id.seeds_connected_textview)TextView seeds_connected_textview;
    @BindView(R.id.buffer_progressbar)ProgressBar buffer_progressbar;
    @BindView(R.id.buffer_progress_sec_textview)TextView buffer_progress_sec_textview;
    @BindView(R.id.buffer_progress_textview)TextView buffer_progress_textview;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torrent_status);
        unbinder=ButterKnife.bind(this);
        GlobalEventBus.getBus().register(this);
        buffer_progressbar.setMax(100);
    }




    private void updateUI(StreamStatus streamStatus)
    {
        if(streamStatus.bufferProgress!=100)
        {
            status_textview.setText("Buffering");
            buffer_progressbar.setProgress(streamStatus.bufferProgress);

        }else
        {
            status_textview.setText("Progress");
            buffer_progressbar.setProgress((int) streamStatus.progress);
        }

        seeds_connected_textview.setText(String.valueOf(streamStatus.seeds));
        download_speed_textview.setText(String.valueOf(streamStatus.downloadSpeed));


    }


    @Subscribe
    public void StreamStatus(Events.StreamStatusBus streamStatusBus)
    {

        updateUI(streamStatusBus.getStreamStatus());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        GlobalEventBus.getBus().unregister(this);
    }
}
