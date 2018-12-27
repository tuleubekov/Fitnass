package com.akay.fitnass.service;

import com.akay.fitnass.data.model.Workout;

import java.util.List;

public interface WorkoutService {

    List<Workout> getAll();

    Workout getById(long id);

    Workout update(Workout workout);

    Workout delete(Workout workout);
}
