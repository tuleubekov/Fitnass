package com.akay.fitnass.data.db.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

public interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(T entity);

    @Delete
    void delete(T entity);
}
