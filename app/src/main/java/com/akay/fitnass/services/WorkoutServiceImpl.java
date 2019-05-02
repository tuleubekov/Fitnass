package com.akay.fitnass.services;

import com.akay.fitnass.data.db.dao.RunsDao;
import com.akay.fitnass.data.db.model.Runs;
import com.akay.fitnass.data.db.model.Workout;
import com.akay.fitnass.scheduler.Scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class WorkoutServiceImpl implements WorkoutService {
    private static WorkoutServiceImpl mInstance;
    private RunsDao mDao;
    private Scheduler<Runs> mScheduler;

    private WorkoutServiceImpl(RunsDao workoutDao, Scheduler<Runs> scheduler) {
        mDao = workoutDao;
        mScheduler = scheduler;
    }

    public static WorkoutService getInstance(RunsDao runsDao, Scheduler<Runs> scheduler) {
        if (mInstance == null) {
            mInstance = new WorkoutServiceImpl(runsDao, scheduler);
        }
        return mInstance;
    }

    @Override
    public List<Runs> getAll() {
        return runWithFutureList(() -> mDao.getAll());
    }

    @Override
    public Runs getById(long id) {
        return runWithFuture(() -> mDao.getById(id));
    }

    @Override
    public void insert(Runs workout) {
        runOnScheduler(() -> mDao.insert(workout));
    }

    @Override
    public Workout update(Runs workout) {
        return null;
    }

    @Override
    public Workout delete(Runs workout) {
        return null;
    }

    private void runOnScheduler(Runnable runnable) {
        mScheduler.runOnThread(runnable);
    }

    private Runs runWithFuture(Callable<Runs> callable) {
        try {
            return mScheduler.runWithFuture(callable);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Runs> runWithFutureList(Callable<List<Runs>> callable) {
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
