package com.techweblearn.mediastreaming.asynctask;

import android.content.ContentResolver;
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

public class DownloadTorrentFileFromContentIntent extends AsyncTask<Uri,Void,File> {

    private ContentResolver contentResolver;
    private HomeActivityModelInter.OnTorrentFileDownloadListener listener;

    public DownloadTorrentFileFromContentIntent(ContentResolver contentResolver, HomeActivityModelInter.OnTorrentFileDownloadListener listener) {
        this.contentResolver = contentResolver;
        this.listener = listener;
    }

    @Override
    protected File doInBackground(Uri... uris) {

        try {

            InputStream inputStream = this.contentResolver.openInputStream(uris[0]);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }


            inputStream.close();
            listener.onCompleted(writeDataToTorrentFile(byteBuffer.toByteArray(),TorrentInfo.bdecode(byteBuffer.toByteArray()).name(), Environment.getExternalStorageDirectory().getAbsolutePath()));

        } catch (IOException e) {
            e.printStackTrace();
            listener.onError("IO EXception");
        }

        return null;
    }


    private File writeDataToTorrentFile(byte[]data,String filename,String storageDir) throws IOException {
        File file=new File(storageDir+"/"+filename+".torrent");

        if(!file.exists())
        {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream=new FileOutputStream(file);

        fileOutputStream.write(data);
        fileOutputStream.close();

        return file;

    }

}
