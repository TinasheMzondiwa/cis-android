package com.tinashe.hymnal.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tinashe.hymnal.data.model.HymnCollection
import com.tinashe.hymnal.data.model.collections.CollectionHymnCrossRef
import com.tinashe.hymnal.data.model.collections.CollectionHymns
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionsDao : BaseDao<HymnCollection> {

    @Query("SELECT * FROM collections ORDER BY title")
    fun listAll(): Flow<List<HymnCollection>>

    @Query("SELECT * FROM collections WHERE title LIKE :query OR description LIKE :query")
    suspend fun searchFor(query: String): List<CollectionHymns>

    @Transaction
    @Query("SELECT * FROM collections")
    fun getCollectionsWithHymns(): Flow<List<CollectionHymns>>

    @Query("SELECT * FROM collectionHymnsRef WHERE collectionId = :collectionId AND hymnId = :hymnId LIMIT 1")
    fun findHymnRef(hymnId: Int, collectionId: Int): CollectionHymnCrossRef?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRef(ref: CollectionHymnCrossRef)

    @Query("DELETE FROM collectionHymnsRef WHERE collectionId = :collectionId AND hymnId = :hymnId")
    suspend fun deleteRef(hymnId: Int, collectionId: Int)
}
