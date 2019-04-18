package com.akay.fitnass.service;

import com.akay.fitnass.data.storage.dao.WorkoutDao;
import com.akay.fitnass.data.storage.model.Workout;
import com.akay.fitnass.scheduler.Scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class WorkoutServiceImpl implements WorkoutService {
    private static WorkoutServiceImpl mInstance;
    private WorkoutDao mDao;
    private Scheduler<Workout> mScheduler;

    private WorkoutServiceImpl() {}

    private WorkoutServiceImpl(WorkoutDao workoutDao, Scheduler<Workout> scheduler) {
        mDao = workoutDao;
        mScheduler = scheduler;
    }

    public static WorkoutService getInstance(WorkoutDao workoutDao, Scheduler<Workout> scheduler) {
        if (mInstance == null) {
            mInstance = new WorkoutServiceImpl(workoutDao, scheduler);
        }
        return mInstance;
    }

    @Override
    public List<Workout> getAll() {
        return runWithFutureList(() -> mDao.getAll());
    }

    @Override
    public Workout getById(long id) {
        return runWithFuture(() -> mDao.getById(id));
    }

    @Override
    public void insert(Workout workout) {
        runOnScheduler(() -> mDao.insert(workout));
    }

    @Override
    public Workout update(Workout workout) {
        return null;
    }

    @Override
    public Workout delete(Workout workout) {
        return null;
    }

    private void runOnScheduler(Runnable runnable) {
        mScheduler.runOnThread(runnable);
    }

    private Workout runWithFuture(Callable<Workout> callable) {
        try {
            return mScheduler.runWithFuture(callable);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Workout> runWithFutureList(Callable<List<Workout>> callable) {
        try {
            return mScheduler.runWithFutureList(callable);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
