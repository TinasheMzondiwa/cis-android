package com.tinashe.hymnal.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.tinashe.hymnal.data.model.HymnCollection

@Dao
interface CollectionsDao : BaseDao<HymnCollection> {

    @Query("SELECT * FROM collections ORDER BY title")
    fun listAll(): List<HymnCollection>
}
