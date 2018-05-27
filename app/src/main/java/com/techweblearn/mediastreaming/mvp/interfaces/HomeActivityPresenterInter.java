package com.techweblearn.mediastreaming.mvp.interfaces;

import android.net.Uri;

public interface HomeActivityPresenterInter {

    void searchTorrentFiles();
    void downloadTorrent(String magnet);
    void getTorrentFile(Uri uri);
}
