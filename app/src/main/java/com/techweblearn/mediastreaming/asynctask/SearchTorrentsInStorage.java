package com.techweblearn.mediastreaming.asynctask;

import android.os.AsyncTask;

import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityModelInter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchTorrentsInStorage extends AsyncTask<String,Integer,List<File>> {


    HomeActivityModelInter.OnTorrentSearchListener listener;

    public SearchTorrentsInStorage(HomeActivityModelInter.OnTorrentSearchListener listener) {
        this.listener = listener;
    }

    private ArrayList<File>fileArrayList=new ArrayList<>();
    @Override
    protected List<File> doInBackground(String... strings) {
        File file=new File(strings[0]);
        getfile(file);
        return fileArrayList;
    }

    @Override
    protected void onPostExecute(List<File> files) {
        super.onPostExecute(files);
        listener.onCompleted(fileArrayList);



    }

    public void getfile(File dir) {

        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                   // fileArrayList.add(listFile[i]);
                    getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".torrent"))

                    {
                        fileArrayList.add(listFile[i]);
                    }
                }

            }
        }



    }
}
