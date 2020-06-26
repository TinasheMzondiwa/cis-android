package com.tinashe.hymnal.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.tinashe.hymnal.data.model.HymnCollection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionsDao : BaseDao<HymnCollection> {

    @Query("SELECT * FROM collections ORDER BY title")
    fun listAll(): Flow<List<HymnCollection>>

    @Query("SELECT * FROM COLLECTIONS WHERE title LIKE :query OR description LIKE :query")
    suspend fun searchFor(query: String): List<HymnCollection>
}
