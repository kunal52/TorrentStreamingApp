package com.techweblearn.mediastreaming.Streaming;

import android.os.Environment;

import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.techweblearn.mediastreaming.mvp.interfaces.HomeActivityModelInter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GetTorrentFromMagnet {

    private HomeActivityModelInter.OnTorrentFileDownloadListener listener;
    public GetTorrentFromMagnet(HomeActivityModelInter.OnTorrentFileDownloadListener listener)
    {
        this.listener=listener;
    }

    public  File getTorrent(String magnet) throws Throwable {

        final String magnet1 = "magnet:?xt=urn:btih:bee75372b98077bfd4de8ef03eb33e9289be5cd8&dn=Avengers+Infinity+War+2018+NEW+PROPER+720p+HD-CAM+X264+HQ-CPG&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
        final SessionManager s = new SessionManager();

        waitForNodesInDHT(s);

        if (magnet.startsWith("magnet:?")) {
            waitForNodesInDHT(s);
            byte[] data = s.fetchMagnet(magnet, 30000);
            TorrentInfo ti = TorrentInfo.bdecode(data);
            s.stop();
            try {
                writeDataToTorrentFile(data,ti.name(),Environment.getExternalStorageDirectory().getAbsolutePath());
                return new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/torrents/"+ti.name()+".torrent");
            } catch (IOException e) {
                e.printStackTrace();
            }



            log(Entry.bdecode(data).toString());

        }

        return null;


    }

    private  void waitForNodesInDHT(final SessionManager s) throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long nodes = s.stats().dhtNodes();
                if (nodes >= 10) {
                    System.out.println("DHT contains " + nodes + " nodes");
                    signal.countDown();
                    timer.cancel();
                }
            }
        }, 0, 1000);

        System.out.println("Waiting for nodes in DHT (10 seconds)...");
        boolean r = signal.await(100, TimeUnit.SECONDS);
        if (!r) {
            System.out.println("DHT bootstrap timeout");
            System.exit(0);
        }
    }

    private static void log(String s) {
        System.out.println(s);
    }


    private void writeDataToTorrentFile(byte[]data,String filename,String storageDir) throws IOException {
        FileOutputStream fileOutputStream=new FileOutputStream(storageDir+"/torrents/"+filename+".torrent");
        fileOutputStream.write(data);
        fileOutputStream.close();

    }



}
