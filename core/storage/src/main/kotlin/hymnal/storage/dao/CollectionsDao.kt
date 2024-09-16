package hymnal.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import hymnal.storage.entity.CollectionHymnCrossRefEntity
import hymnal.storage.entity.CollectionHymnsEntity
import hymnal.storage.entity.HymnCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionsDao : BaseDao<HymnCollectionEntity> {

    @Transaction
    @Query("SELECT * FROM collections WHERE collectionId = :id LIMIT 1")
    suspend fun findById(id: Int): CollectionHymnsEntity?

    @Transaction
    @Query("SELECT * FROM collections WHERE title LIKE :query OR description LIKE :query")
    suspend fun searchFor(query: String): List<CollectionHymnsEntity>

    @Transaction
    @Query("SELECT * FROM collections ORDER BY created DESC")
    fun getCollectionsWithHymns(): Flow<List<CollectionHymnsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRef(ref: CollectionHymnCrossRefEntity)

    @Delete
    suspend fun deleteRef(ref: CollectionHymnCrossRefEntity)

    @Transaction
    @Query("SELECT * FROM collectionHymnsRef WHERE hymnId = :hymnId AND collectionId = :collectionId")
    suspend fun findRef(hymnId: Int, collectionId: Int): CollectionHymnCrossRefEntity?

    @Query("DELETE FROM collections WHERE collectionId = :id")
    suspend fun deleteCollection(id: Int)
}
