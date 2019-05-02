package com.akay.fitnass.data.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.akay.fitnass.data.storage.model.Runs;

import java.util.List;

@Dao
public interface RunsDao extends BaseDao<Runs> {

    @Query("SELECT * FROM runs ORDER BY dateTime DESC")
    List<Runs> getAll();

    @Query("SELECT * FROM runs WHERE id = :id")
    Runs getById(final long id);
}
