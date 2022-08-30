package com.tinashe.hymnal.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: T)

    @Delete
    suspend fun delete(item: T)
}
