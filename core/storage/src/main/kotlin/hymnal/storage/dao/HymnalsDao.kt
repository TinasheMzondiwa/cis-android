package hymnal.storage.dao

import androidx.room.Dao
import androidx.room.Query
import hymnal.storage.entity.HymnalEntity

@Dao
interface HymnalsDao : BaseDao<HymnalEntity> {

    @Query("SELECT * FROM hymnals")
    suspend fun listAll(): List<HymnalEntity>

    @Query("SELECT * FROM hymnals WHERE code =:code")
    fun findByCode(code: String): HymnalEntity?
}
