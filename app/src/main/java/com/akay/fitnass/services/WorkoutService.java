package com.akay.fitnass.services;

import com.akay.fitnass.data.storage.model.Workout;

import java.util.List;

public interface WorkoutService {

    List<Workout> getAll();

    Workout getById(long id);

    void insert(Workout workout);

    Workout update(Workout workout);

    Workout delete(Workout workout);
}
