package com.techweblearn.mediastreaming.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.R;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;



public class TorrentProcessingDialogFragment extends DialogFragment {

    @BindView(R.id.buffer_progress)TextView bufferProgress;
    @BindView(R.id.message)TextView message;

    public static TorrentProcessingDialogFragment getInstance()
    {
        return new TorrentProcessingDialogFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalEventBus.getBus().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.torrent_processing_dialog_layout,container,false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalEventBus.getBus().unregister(this);
    }

    @Subscribe
    public void getStreamStatus(Events.StreamStatusBus streamStatusBus)
    {
        bufferProgress.setText(String.valueOf(streamStatusBus.getStreamStatus().bufferProgress));

    }

}
