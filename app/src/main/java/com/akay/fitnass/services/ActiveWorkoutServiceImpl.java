package com.akay.fitnass.services;

import com.akay.fitnass.data.db.dao.ActiveRunsDao;
import com.akay.fitnass.data.db.model.ActiveRuns;
import com.akay.fitnass.scheduler.Scheduler;
import com.akay.fitnass.util.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ActiveWorkoutServiceImpl implements ActiveWorkoutService {
    private static ActiveWorkoutServiceImpl mInstance;
    private ActiveRunsDao mDao;
    private Scheduler<ActiveRuns> mScheduler;

    private ActiveWorkoutServiceImpl(ActiveRunsDao activeRunsDao, Scheduler<ActiveRuns> scheduler) {
        mDao = activeRunsDao;
        mScheduler = scheduler;
    }

    public static ActiveWorkoutService getInstance(ActiveRunsDao activeRunsDao, Scheduler<ActiveRuns> scheduler) {
        if (mInstance == null) {
            mInstance = new ActiveWorkoutServiceImpl(activeRunsDao, scheduler);
        }
        return mInstance;
    }

    @Override
    public ActiveRuns getActiveRuns() {
        return runWithFuture(() -> mDao.getActiveRuns());
    }

    @Override
    public void delete(ActiveRuns workout) {
        workout.setId(ActiveRuns.ID);
        runOnScheduler(() -> mDao.delete(workout));
    }

    @Override
    public void upsert(ActiveRuns workout) {
        Logger.e("---- upsert ----");
        workout.setId(ActiveRuns.ID);
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

    private ActiveRuns runWithFuture(Callable<ActiveRuns> callable) {
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
