package com.techweblearn.mediastreaming.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerInfo implements Parcelable {

    private long bufferedProgress;
    private int bufferedPercentage;
    private long currentProgress;

    public PlayerInfo() {
    }

    public PlayerInfo(long bufferedProgress, int bufferedPercentage, long currentProgress) {
        this.bufferedProgress = bufferedProgress;
        this.bufferedPercentage = bufferedPercentage;
        this.currentProgress = currentProgress;
    }

    protected PlayerInfo(Parcel in) {
        bufferedProgress = in.readLong();
        bufferedPercentage = in.readInt();
        currentProgress = in.readLong();
    }

    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel in) {
            return new PlayerInfo(in);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };

    public long getBufferedProgress() {
        return bufferedProgress;
    }

    public void setBufferedProgress(long bufferedProgress) {
        this.bufferedProgress = bufferedProgress;
    }

    public int getBufferedPercentage() {
        return bufferedPercentage;
    }

    public void setBufferedPercentage(int bufferedPercentage) {
        this.bufferedPercentage = bufferedPercentage;
    }

    public long getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(long currentProgress) {
        this.currentProgress = currentProgress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(bufferedProgress);
        dest.writeInt(bufferedPercentage);
        dest.writeLong(currentProgress);
    }
}
