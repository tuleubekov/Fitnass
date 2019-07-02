package com.akay.fitnass.data.db.dao

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Update

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: T)

    @Delete
    fun delete(entity: T)
}
