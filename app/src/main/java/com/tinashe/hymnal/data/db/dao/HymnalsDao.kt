package com.tinashe.hymnal.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.tinashe.hymnal.data.model.Hymnal

@Dao
interface HymnalsDao : BaseDao<Hymnal> {

    @Query("SELECT * FROM hymnals ORDER BY title")
    fun listAll(): List<Hymnal>
}