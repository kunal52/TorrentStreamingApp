package com.techweblearn.mediastreaming.mvp.classes;

import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityModelInter;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityPresenterInter;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityViewInter;

import java.io.File;
import java.util.ArrayList;

public class HomeActivityPresenter implements HomeActivityPresenterInter, HomeActivityModelInter.OnTorrentSearchListener {

    HomeActivityViewInter homeActivityViewInter;
    HomeActivityModelInter homeActivityModelInter;

    public HomeActivityPresenter(HomeActivityViewInter homeActivityViewInter) {
        this.homeActivityViewInter = homeActivityViewInter;
        homeActivityModelInter=new HomeActivityModel();
    }

    @Override
    public void searchTorrentFiles() {
        homeActivityModelInter.searchTorrentFiles(this);
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
}
