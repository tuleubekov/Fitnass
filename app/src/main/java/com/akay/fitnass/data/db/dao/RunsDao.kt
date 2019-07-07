package com.akay.fitnass.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

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
