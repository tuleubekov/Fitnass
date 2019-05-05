package com.akay.fitnass.data;

import android.arch.lifecycle.LiveData;

import com.akay.fitnass.data.db.dao.ActiveRunsDao;
import com.akay.fitnass.data.db.dao.RunsDao;
import com.akay.fitnass.data.model.ActiveRuns;
import com.akay.fitnass.data.model.Runs;

import java.util.List;

public class RunsRepositoryImpl implements RunsRepository {
    private final ActiveRunsDao mActiveRunsDao;
    private final RunsDao mRunsDao;
    private final LiveData<ActiveRuns> mActiveRuns;
    private final LiveData<List<Runs>> mRunsList;

    public RunsRepositoryImpl(ActiveRunsDao activeRunsDao, RunsDao runsDao) {
        mActiveRunsDao = activeRunsDao;
        mRunsDao = runsDao;

        mActiveRuns = mActiveRunsDao.getLiveActiveRuns();
        mRunsList = mRunsDao.getAllLive();
    }

    @Override
    public LiveData<ActiveRuns> getActiveRuns() {
        return mActiveRuns;
    }

    @Override
    public LiveData<List<Runs>> getRuns() {
        return mRunsList;
    }

    @Override
    public Runs getById(long id) {
        return mRunsDao.getById(id);
    }

    @Override
    public void upsertActiveRuns(ActiveRuns activeRuns) {
        activeRuns.setId(ActiveRuns.ID);
        mActiveRunsDao.upsert(activeRuns);
    }

    @Override
    public void deleteActiveRuns() {
        mActiveRunsDao.deleteActiveRuns();
    }

    @Override
    public void saveRuns(Runs runs) {
        mRunsDao.insert(runs);
    }
}
