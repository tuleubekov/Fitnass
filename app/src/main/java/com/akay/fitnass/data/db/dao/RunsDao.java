package com.akay.fitnass.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.akay.fitnass.data.model.Runs;

import java.util.List;

@Dao
public interface RunsDao extends BaseDao<Runs> {

    @Query("SELECT * FROM runs ORDER BY dateTime DESC")
    List<Runs> getAll();

    @Query("SELECT * FROM runs ORDER BY dateTime DESC")
    LiveData<List<Runs>> getAllLive();

    @Query("SELECT * FROM runs WHERE id = :id")
    Runs getById(final long id);
}
