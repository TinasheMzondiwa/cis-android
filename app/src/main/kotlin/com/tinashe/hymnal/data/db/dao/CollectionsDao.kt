package com.tinashe.hymnal.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tinashe.hymnal.data.model.collections.CollectionHymnCrossRef
import com.tinashe.hymnal.data.model.collections.CollectionHymns
import com.tinashe.hymnal.data.model.collections.HymnCollection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionsDao : BaseDao<HymnCollection> {

    @Transaction
    @Query("SELECT * FROM collections WHERE collectionId = :id LIMIT 1")
    suspend fun findById(id: Int): CollectionHymns?

    @Transaction
    @Query("SELECT * FROM collections WHERE title LIKE :query OR description LIKE :query")
    suspend fun searchFor(query: String): List<CollectionHymns>

    @Transaction
    @Query("SELECT * FROM collections ORDER BY created DESC")
    fun getCollectionsWithHymns(): Flow<List<CollectionHymns>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRef(ref: CollectionHymnCrossRef)

    @Delete
    suspend fun deleteRef(ref: CollectionHymnCrossRef)

    @Transaction
    @Query("SELECT * FROM collectionHymnsRef WHERE hymnId = :hymnId AND collectionId = :collectionId")
    suspend fun findRef(hymnId: Int, collectionId: Int): CollectionHymnCrossRef?

    @Query("DELETE FROM collections WHERE collectionId = :id")
    suspend fun deleteCollection(id: Int)
}
