package com.techweblearn.mediastreaming.mvp.classes;

import android.os.Environment;

import com.techweblearn.mediastreaming.asynctask.SearchTorrentsInStorage;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityModelInter;

public class HomeActivityModel implements HomeActivityModelInter {
    @Override
    public void searchTorrentFiles(OnTorrentSearchListener listener) {

        SearchTorrentsInStorage searchTorrentsInStorage=new SearchTorrentsInStorage(listener);
        searchTorrentsInStorage.execute(Environment.getExternalStorageDirectory().getAbsolutePath());


    }
}
