package com.akay.fitnass.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public interface Scheduler<T> {

    void runOnThread(Runnable runnable);

    T runWithFuture(Callable<T> callable) throws ExecutionException, InterruptedException;

    long runWithFutureLong(Callable<Long> callable) throws ExecutionException, InterruptedException;

    List<T> runWithFutureList(Callable<List<T>> callable) throws ExecutionException, InterruptedException;
}
