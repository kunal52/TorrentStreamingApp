package com.techweblearn.mediastreaming.mvp.interfaces;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

public interface HomeActivityModelInter {

    interface OnTorrentSearchListener
    {
        void onCompleted(ArrayList<File>files);
        void onError();
        void onPermissionError();
    }

    interface OnTorrentFileDownloadListener
    {
        void onCompleted(File file);
        void onError(String message);
    }

    void searchTorrentFiles(OnTorrentSearchListener listener);
    void downloadTorrent(String magnet,OnTorrentFileDownloadListener listener);
    void getFileFromUri(Uri uri,OnTorrentFileDownloadListener listener);


}
