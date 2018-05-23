package com.techweblearn.mediastreaming.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "recentplay")
public class RecentPlayHistory {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    private int id;


    @ColumnInfo(name = "filename")
    private String filename;


    @ColumnInfo(name = "magnetlink")
    private String magnetlink;


    @ColumnInfo(typeAffinity = ColumnInfo.BLOB,name = "filedata")
    private byte[] torrentfiledata;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMagnetlink() {
        return magnetlink;
    }

    public void setMagnetlink(String magnetlink) {
        this.magnetlink = magnetlink;
    }

    public byte[] getTorrentfiledata() {
        return torrentfiledata;
    }

    public void setTorrentfiledata(byte[] torrentfiledata) {
        this.torrentfiledata = torrentfiledata;
    }
}
