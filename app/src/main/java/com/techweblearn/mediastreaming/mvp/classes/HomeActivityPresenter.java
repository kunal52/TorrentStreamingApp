package com.techweblearn.mediastreaming.mvp.classes;

import android.content.ContentResolver;
import android.net.Uri;

import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityModelInter;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityPresenterInter;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityViewInter;

import java.io.File;
import java.util.ArrayList;

public class HomeActivityPresenter implements HomeActivityPresenterInter, HomeActivityModelInter.OnTorrentSearchListener, HomeActivityModelInter.OnTorrentFileDownloadListener {

    private HomeActivityViewInter homeActivityViewInter;
    private HomeActivityModelInter homeActivityModelInter;

    public HomeActivityPresenter(ContentResolver contentResolver,HomeActivityViewInter homeActivityViewInter) {
        this.homeActivityViewInter = homeActivityViewInter;
        homeActivityModelInter=new HomeActivityModel(contentResolver);
    }

    @Override
    public void searchTorrentFiles() {
        homeActivityModelInter.searchTorrentFiles(this);
    }

    @Override
    public void downloadTorrent(String magnet) {
        homeActivityModelInter.downloadTorrent(magnet,this);
    }

    @Override
    public void getTorrentFile(Uri uri) {
        homeActivityModelInter.getFileFromUri(uri,this);
    }

    @Override
    public void onCompleted(ArrayList<File> files) {
        homeActivityViewInter.onCompleted(files);
    }

    @Override
    public void onError() {
        homeActivityViewInter.onError();
    }

    @Override
    public void onPermissionError() {
        homeActivityViewInter.onPermissionError();
    }

    @Override
    public void onCompleted(File file) {
        homeActivityViewInter.onCompleted(file);
    }

    @Override
    public void onError(String message) {
        homeActivityViewInter.onError(message);
    }
}
