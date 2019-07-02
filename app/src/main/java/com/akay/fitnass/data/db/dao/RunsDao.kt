package com.akay.fitnass.data.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

import com.akay.fitnass.data.model.Runs

@Dao
interface RunsDao : BaseDao<Runs> {

    @Query("SELECT * FROM runs ORDER BY dateTime DESC")
    fun getAll(): List<Runs>

    @Query("SELECT * FROM runs ORDER BY dateTime DESC")
    fun getAllLive(): LiveData<List<Runs>>

    @Query("SELECT * FROM runs WHERE id = :id")
    fun getById(id: Long): Runs
}
