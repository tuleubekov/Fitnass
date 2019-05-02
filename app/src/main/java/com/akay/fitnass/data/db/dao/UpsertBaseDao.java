package com.akay.fitnass.data.db.dao;

public abstract class UpsertBaseDao<T> implements BaseDao<T> {

    public void upsert(T entity) {
        long id = insert(entity);
        if (id == -1) {
            update(entity);
        }
    }
}