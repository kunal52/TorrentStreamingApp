package com.techweblearn.mediastreaming.database.entities;

import android.arch.persistence.room.Entity;

@Entity(tableName = "files_table")
public class TorrentFiles {

    private String filename;
    private String filePath;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
