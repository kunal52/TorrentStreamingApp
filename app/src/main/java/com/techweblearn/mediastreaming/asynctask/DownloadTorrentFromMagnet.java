package com.techweblearn.mediastreaming.asynctask;

import android.os.AsyncTask;

import com.techweblearn.mediastreaming.Streaming.GetTorrentFromMagnet;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityModelInter;

import java.io.File;

public class DownloadTorrentFromMagnet extends AsyncTask<String,Void,File> {

    private HomeActivityModelInter.OnTorrentFileDownloadListener listener;
    public DownloadTorrentFromMagnet(HomeActivityModelInter.OnTorrentFileDownloadListener listener)
    {
        this.listener=listener;
    }

    @Override
    protected File doInBackground(String... strings) {
        try {
            GetTorrentFromMagnet getTorrentFromMagnet=new GetTorrentFromMagnet(listener);
            return getTorrentFromMagnet.getTorrent(strings[0]);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if(file!=null)
        listener.onCompleted(file);
        else listener.onError("Error");
    }
}
