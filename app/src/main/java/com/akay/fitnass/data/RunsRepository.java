package com.akay.fitnass.data;

import android.arch.lifecycle.LiveData;

import com.akay.fitnass.data.model.ActiveRuns;
import com.akay.fitnass.data.model.Runs;

import java.util.List;

public interface RunsRepository {

    LiveData<ActiveRuns> getActiveRuns();

    LiveData<List<Runs>> getRuns();

    Runs getById(long id);

    void upsertActiveRuns(ActiveRuns activeRuns);

    void deleteActiveRuns();

    void saveRuns(Runs runs);
}
