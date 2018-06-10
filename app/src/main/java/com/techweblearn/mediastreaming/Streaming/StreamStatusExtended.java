package com.techweblearn.mediastreaming.Streaming;

import android.os.Parcel;
import android.os.Parcelable;

public class StreamStatusExtended implements Parcelable {


    private  float progress;
    private  int bufferProgress;
    private  int seeds;
    private  float downloadSpeed;
    private  long size;
    private  int bitrate;
    private  long duration;
    private long downloadedBytes;


    public StreamStatusExtended() {
    }

    public StreamStatusExtended(float progress, int bufferProgress, int seeds, float downloadSpeed, long size, int bitrate, long duration, long downloadedBytes) {
        this.progress = progress;
        this.bufferProgress = bufferProgress;
        this.seeds = seeds;
        this.downloadSpeed = downloadSpeed;
        this.size = size;
        this.bitrate = bitrate;
        this.duration = duration;
        this.downloadedBytes = downloadedBytes;
    }

    protected StreamStatusExtended(Parcel in) {
        progress = in.readFloat();
        bufferProgress = in.readInt();
        seeds = in.readInt();
        downloadSpeed = in.readFloat();
        size = in.readLong();
        bitrate = in.readInt();
        duration = in.readLong();
        downloadedBytes = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(progress);
        dest.writeInt(bufferProgress);
        dest.writeInt(seeds);
        dest.writeFloat(downloadSpeed);
        dest.writeLong(size);
        dest.writeInt(bitrate);
        dest.writeLong(duration);
        dest.writeLong(downloadedBytes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StreamStatusExtended> CREATOR = new Creator<StreamStatusExtended>() {
        @Override
        public StreamStatusExtended createFromParcel(Parcel in) {
            return new StreamStatusExtended(in);
        }

        @Override
        public StreamStatusExtended[] newArray(int size) {
            return new StreamStatusExtended[size];
        }
    };

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getBufferProgress() {
        return bufferProgress;
    }

    public void setBufferProgress(int bufferProgress) {
        this.bufferProgress = bufferProgress;
    }

    public int getSeeds() {
        return seeds;
    }

    public void setSeeds(int seeds) {
        this.seeds = seeds;
    }

    public float getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(float downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    @Override
    public String toString() {
        return "StreamStatusExtended{" +
                "progress=" + progress +
                ", bufferProgress=" + bufferProgress +
                ", seeds=" + seeds +
                ", downloadSpeed=" + downloadSpeed +
                ", size=" + size +
                ", bitrate=" + bitrate +
                ", duration=" + duration +
                ", downloadedBytes=" + downloadedBytes +
                '}';
    }
}
