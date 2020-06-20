package com.tinashe.hymnal.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.tinashe.hymnal.data.model.Hymn

@Dao
interface HymnsDao : BaseDao<Hymn> {

    @Query("SELECT * FROM hymns WHERE book =:code ORDER BY number")
    fun listAllPaged(code: String): PagingSource<Int, Hymn>

    @Query("SELECT * FROM hymns WHERE book =:code ORDER BY number")
    fun listAll(code: String): List<Hymn>

    @Query("SELECT * FROM hymns WHERE title LIKE :query AND book =:code OR content LIKE :query AND book =:code")
    suspend fun search(code: String, query: String): List<Hymn>
}
