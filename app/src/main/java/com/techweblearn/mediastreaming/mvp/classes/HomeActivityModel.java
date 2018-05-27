package com.techweblearn.mediastreaming.mvp.classes;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.techweblearn.mediastreaming.asynctask.DownloadTorrentFileFromContentIntent;
import com.techweblearn.mediastreaming.asynctask.DownloadTorrentFileFromHttp;
import com.techweblearn.mediastreaming.asynctask.DownloadTorrentFromMagnet;
import com.techweblearn.mediastreaming.asynctask.SearchTorrentsInStorage;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityModelInter;

import java.io.File;

public class HomeActivityModel implements HomeActivityModelInter {

    public static final String TAG=HomeActivityModel.class.getSimpleName();
    private ContentResolver contentResolver;

    public HomeActivityModel(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public void searchTorrentFiles(OnTorrentSearchListener listener) {

        SearchTorrentsInStorage searchTorrentsInStorage=new SearchTorrentsInStorage(listener);
        searchTorrentsInStorage.execute(Environment.getExternalStorageDirectory().getAbsolutePath());


    }

    @Override
    public void downloadTorrent(String magnet, OnTorrentFileDownloadListener listener) {


        DownloadTorrentFromMagnet downloadTorrentFromMagnet=new DownloadTorrentFromMagnet(listener);
        downloadTorrentFromMagnet.execute(magnet);
        Log.d("Download","Torrent");

    }

    @Override
    public void getFileFromUri(Uri uri, OnTorrentFileDownloadListener listener) {


        Log.d(TAG,uri.toString());
//        Log.d(TAG,uri.getPath());

        if(uri.toString().startsWith("magnet"))
        {
            DownloadTorrentFromMagnet downloadTorrentFromMagnet=new DownloadTorrentFromMagnet(listener);
            downloadTorrentFromMagnet.execute(uri.toString());
        }
        if(uri.toString().startsWith("file"))
        {
            listener.onCompleted(new File(uri.getPath()));
        }
        if (uri.toString().startsWith("http")||uri.toString().startsWith("https"))
        {
            DownloadTorrentFileFromHttp downloadTorrentFileFromHttp=new DownloadTorrentFileFromHttp(listener);
            downloadTorrentFileFromHttp.execute(uri);
        }
        if(uri.toString().startsWith("content"))
        {
            DownloadTorrentFileFromContentIntent downloadTorrentFileFromContentIntent=new DownloadTorrentFileFromContentIntent(contentResolver,listener);
            downloadTorrentFileFromContentIntent.execute(uri);
        }


    }
}
