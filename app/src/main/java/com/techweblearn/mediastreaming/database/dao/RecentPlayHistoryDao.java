package com.techweblearn.mediastreaming.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.techweblearn.mediastreaming.database.entities.RecentPlayHistory;

import java.util.List;

@Dao
public interface RecentPlayHistoryDao {

    @Query("Select * from recentplay")
    List<RecentPlayHistory>loadAll();


    @Query("select * from recentplay where filename = :filename")
    RecentPlayHistory getFileData(String filename);

    @Insert
    void insert(RecentPlayHistory recentPlayHistory);

    @Delete
    void delete(RecentPlayHistory recentPlayHistory);



}
