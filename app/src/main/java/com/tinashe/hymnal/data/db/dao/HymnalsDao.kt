package com.tinashe.hymnal.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.HymnalHymns

@Dao
interface HymnalsDao : BaseDao<Hymnal> {

    @Query("SELECT * FROM hymnals ORDER BY title")
    fun listAll(): List<Hymnal>

    @Transaction
    @Query("SELECT * FROM hymnals WHERE code =:code")
    fun getHymns(code: String): HymnalHymns?
}