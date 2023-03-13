package hymnal.storage.dao

import androidx.room.Dao
import androidx.room.Query
import hymnal.storage.entity.HymnEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HymnsDao : BaseDao<HymnEntity> {

    @Query("SELECT * FROM hymns WHERE book = :code ORDER BY number")
    fun listAll(code: String): Flow<List<HymnEntity>>

    @Query("SELECT * FROM hymns WHERE title LIKE :query AND book =:code OR content LIKE :query AND book =:code")
    suspend fun search(code: String, query: String): List<HymnEntity>

    @Query("SELECT * FROM hymns WHERE number =:number AND book =:code LIMIT 1")
    suspend fun findByNumber(number: Int, code: String): HymnEntity?
}
