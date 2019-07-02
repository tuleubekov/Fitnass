package com.akay.fitnass.data

import android.arch.lifecycle.LiveData

import com.akay.fitnass.data.db.dao.ActiveRunsDao
import com.akay.fitnass.data.db.dao.RunsDao
import com.akay.fitnass.data.model.ActiveRuns
import com.akay.fitnass.data.model.Runs

class RunsRepositoryImpl(private val mActiveRunsDao: ActiveRunsDao, private val mRunsDao: RunsDao) : RunsRepository {

    private val mActiveRuns: LiveData<ActiveRuns> = mActiveRunsDao.getLiveActiveRuns()
    private val mRunsList: LiveData<List<Runs>> = mRunsDao.getAllLive()

    override fun getActiveRuns(): ActiveRuns {
        return mActiveRunsDao.getActiveRuns()
    }

    override fun getLiveActiveRuns(): LiveData<ActiveRuns> = mActiveRuns

    override fun getLiveRuns(): LiveData<List<Runs>> = mRunsList

    override fun getById(id: Long): Runs = mRunsDao.getById(id)

    override fun upsertActiveRuns(activeRuns: ActiveRuns) {
        activeRuns.id = ActiveRuns.ID
        mActiveRunsDao.insert(activeRuns)
    }

    override fun deleteActiveRuns() = mActiveRunsDao.deleteActiveRuns()

    override fun saveRuns(runs: Runs) = mRunsDao.insert(runs)

    override fun deleteRuns(runs: Runs) = mRunsDao.delete(runs)
}
