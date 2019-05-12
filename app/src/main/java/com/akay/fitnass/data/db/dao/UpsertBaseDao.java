package com.akay.fitnass.data.db.dao;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Transaction;

public abstract class UpsertBaseDao<T> implements BaseDao<T> {

//    @Transaction
//    public void upsert(T entity) {
//        long id = insert(entity);
//        if (id == -1) {
//            update(entity);
//        }
//    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsert(T entity);
}
