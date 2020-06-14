package com.tinashe.hymnal.data.db.dao

import androidx.room.Dao
import com.tinashe.hymnal.data.model.Hymn

@Dao
interface HymnsDao : BaseDao<Hymn> {
}