package com.akay.fitnass.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.akay.fitnass.data.model.Workout;

import java.util.List;

@Dao
public interface WorkoutDao {

    @Query("SELECT * FROM workout ORDER BY date DESC")
    List<Workout> getAll();

    @Query("SELECT * FROM workout WHERE id = :id")
    Workout getById(long id);

    @Insert
    void insert(Workout workout);

    @Update
    void update(Workout workout);

    @Delete
    void delete(Workout workout);
}
