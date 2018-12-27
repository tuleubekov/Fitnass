package com.akay.fitnass.service;

import com.akay.fitnass.data.dao.WorkoutDao;
import com.akay.fitnass.data.model.Workout;

import java.util.List;

public class WorkoutServiceImpl implements WorkoutService {
    private WorkoutDao mDao;

    public WorkoutServiceImpl(WorkoutDao workoutDao) {
        mDao = workoutDao;
    }

    @Override
    public List<Workout> getAll() {
        return mDao.getAll();
    }
}
