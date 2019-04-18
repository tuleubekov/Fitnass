package com.akay.fitnass.data.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.akay.fitnass.data.storage.model.ActiveWorkout;

@Dao
public abstract class ActiveWorkoutDao {

    @Query("SELECT * FROM active_workout WHERE id = :id")
    public abstract ActiveWorkout getById(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(ActiveWorkout workout);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void update(ActiveWorkout workout);

    @Delete
    public abstract void delete(ActiveWorkout workout);

    public void upsert(ActiveWorkout entity) {
        long id = insert(entity);
        if (id == -1) {
            update(entity);
        }
    }

}
