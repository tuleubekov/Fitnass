package com.akay.fitnass.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class SingleThreadScheduler<T> implements Scheduler<T> {
    private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
    private static SingleThreadScheduler mInstance;

    private SingleThreadScheduler() {}

    public static <T> Scheduler<T> getInstance() {
        if (mInstance == null) {
            mInstance = new SingleThreadScheduler<T>();
        }
        return mInstance;
    }

    @Override
    public void runOnThread(Runnable runnable) {
        SINGLE_THREAD_EXECUTOR.execute(runnable);
    }

    @Override
    public T runWithFuture(Callable<T> callable) throws ExecutionException, InterruptedException {
        FutureTask<T> futureTask = new FutureTask<>(callable);
        SINGLE_THREAD_EXECUTOR.submit(futureTask);
        return futureTask.get();
    }

    @Override
    public List<T> runWithFutureList(Callable<List<T>> callable) throws ExecutionException, InterruptedException {
        FutureTask<List<T>> futureTask = new FutureTask<>(callable);
        SINGLE_THREAD_EXECUTOR.submit(futureTask);
        return futureTask.get();
    }
}
