package com.akay.fitnass.services;

import com.akay.fitnass.data.db.model.Runs;
import com.akay.fitnass.data.db.model.Workout;

import java.util.List;

public interface WorkoutService {

    List<Runs> getAll();

    Runs getById(long id);

    void insert(Runs workout);

    Workout update(Runs workout);

    Workout delete(Runs workout);
}
