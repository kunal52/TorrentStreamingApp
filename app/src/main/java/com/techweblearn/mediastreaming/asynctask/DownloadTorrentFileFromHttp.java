package com.techweblearn.mediastreaming.asynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.frostwire.jlibtorrent.TorrentInfo;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityModelInter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTorrentFileFromHttp extends AsyncTask<Uri,Void,File> {

    private HomeActivityModelInter.OnTorrentFileDownloadListener torrentFileDownloadListener;

    public DownloadTorrentFileFromHttp(HomeActivityModelInter.OnTorrentFileDownloadListener torrentFileDownloadListener) {
        this.torrentFileDownloadListener = torrentFileDownloadListener;
    }

    @Override
    protected File doInBackground(Uri... uris) {

        byte[]responseByteArray = new byte[0];
        URL url = null;
        try {
            url = new URL(uris[0].toString());

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        if (connection.getResponseCode() == 200) {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            responseByteArray = byteBuffer.toByteArray();
        }

            inputStream.close();
            connection.disconnect();

        torrentFileDownloadListener.onCompleted(writeDataToTorrentFile(responseByteArray,TorrentInfo.bdecode(responseByteArray).name(), Environment.getExternalStorageDirectory().getAbsolutePath()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private File writeDataToTorrentFile(byte[]data,String filename,String storageDir) throws IOException {
        FileOutputStream fileOutputStream=new FileOutputStream(storageDir+"/torrents/"+filename+".torrent");
        fileOutputStream.write(data);
        fileOutputStream.close();

        return new File(storageDir+"/torrents/"+filename+".torrent");

    }
}
