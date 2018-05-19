package com.techweblearn.mediastreaming.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoInfo implements Parcelable {

    private String title;
    private String filePath;
    private long totalSize;     //bytes
    private int bitrate;        //bits
    private long duration;      //ms


    public VideoInfo(String title, String filePath, long totalSize, int bitrate, long duration) {
        this.title = title;
        this.filePath = filePath;
        this.totalSize = totalSize;
        this.bitrate = bitrate;
        this.duration = duration;
    }

    protected VideoInfo(Parcel in) {
        title = in.readString();
        filePath = in.readString();
        totalSize = in.readLong();
        bitrate = in.readInt();
        duration = in.readLong();
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public int getBitrate() {
        return bitrate;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(filePath);
        dest.writeLong(totalSize);
        dest.writeInt(bitrate);
        dest.writeLong(duration);
    }
}
