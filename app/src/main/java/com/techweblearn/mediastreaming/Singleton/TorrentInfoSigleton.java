package com.techweblearn.mediastreaming.Singleton;

public class TorrentInfoSigleton {
    private static final TorrentInfoSigleton ourInstance = new TorrentInfoSigleton();

    public static TorrentInfoSigleton getInstance() {
        return ourInstance;
    }

    private TorrentInfoSigleton() {
    }


    private int bufferProgress;
    private float downloadProgress;
    private long playProgress;
    private long bitrate;
    private long totalSize;
    private long totalDuration;
    private String title;


    public int getBufferProgress() {
        return bufferProgress;
    }

    public void setBufferProgress(int bufferProgress) {
        this.bufferProgress = bufferProgress;
    }

    public float getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(float downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public long getPlayProgress() {
        return playProgress;
    }

    public void setPlayProgress(long playProgress) {
        this.playProgress = playProgress;
    }

    public long getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
