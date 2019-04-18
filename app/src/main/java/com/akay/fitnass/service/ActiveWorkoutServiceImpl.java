package com.akay.fitnass.service;

import com.akay.fitnass.data.storage.dao.ActiveWorkoutDao;
import com.akay.fitnass.data.storage.model.ActiveWorkout;
import com.akay.fitnass.scheduler.Scheduler;
import com.akay.fitnass.util.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ActiveWorkoutServiceImpl implements ActiveWorkoutService {
    private static ActiveWorkoutServiceImpl mInstance;
    private ActiveWorkoutDao mDao;
    private Scheduler<ActiveWorkout> mScheduler;

    private ActiveWorkoutServiceImpl() {}

    private ActiveWorkoutServiceImpl(ActiveWorkoutDao activeWorkoutDao, Scheduler<ActiveWorkout> scheduler) {
        mDao = activeWorkoutDao;
        mScheduler = scheduler;
    }

    public static ActiveWorkoutService getInstance(ActiveWorkoutDao activeWorkoutDao, Scheduler<ActiveWorkout> scheduler) {
        if (mInstance == null) {
            mInstance = new ActiveWorkoutServiceImpl(activeWorkoutDao, scheduler);
        }
        return mInstance;
    }

    @Override
    public ActiveWorkout getActiveSession() {
        return runWithFuture(() -> mDao.getById(ActiveWorkout.ID));
    }

    @Override
    public long insert(ActiveWorkout activeWorkout) {
        return runWithFutureLong(() -> mDao.insert(activeWorkout));
    }

    @Override
    public void update(ActiveWorkout workout) {
        runOnScheduler(() -> mDao.update(workout));
    }

    @Override
    public void delete(ActiveWorkout workout) {
        runOnScheduler(() -> mDao.delete(workout));
    }

    @Override
    public void upsert(ActiveWorkout workout) {
        Logger.e("---- upsert ----");
        runOnScheduler(() -> mDao.upsert(workout));
    }

    private void runOnScheduler(Runnable runnable) {
        mScheduler.runOnThread(runnable);
    }

    private long runWithFutureLong(Callable<Long> callable) {
        try {
            return mScheduler.runWithFutureLong(callable);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private ActiveWorkout runWithFuture(Callable<ActiveWorkout> callable) {
        try {
            return mScheduler.runWithFuture(callable);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
