package com.akay.fitnass.service;

import com.akay.fitnass.data.storage.model.ActiveWorkout;

public interface ActiveWorkoutService {

    ActiveWorkout getActiveSession();

    long insert(ActiveWorkout activeWorkout);

    void update(ActiveWorkout workout);

    void delete(ActiveWorkout workout);

    void upsert(ActiveWorkout workout);
}
