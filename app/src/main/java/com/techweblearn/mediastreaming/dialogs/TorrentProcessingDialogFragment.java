package com.techweblearn.mediastreaming.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;
import com.techweblearn.mediastreaming.R;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TorrentProcessingDialogFragment extends DialogFragment implements View.OnClickListener{


    @BindView(R.id.cancel_button)Button cancel_button;
    Unbinder unbinder;

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
        View view= inflater.inflate(R.layout.torrent_processing_dialog_layout,container,false);
        unbinder= ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancel_button.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalEventBus.getBus().unregister(this);
    }

    @Subscribe
    public void getStreamStatus(Events.StreamStatusBus streamStatusBus)
    {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();

    }
}
