package com.tinashe.hymnal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tinashe.hymnal.data.db.dao.CollectionsDao
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.collections.CollectionHymnCrossRef
import com.tinashe.hymnal.data.model.collections.HymnCollection

@Database(
    entities = [
        Hymnal::class,
        Hymn::class,
        HymnCollection::class,
        CollectionHymnCrossRef::class
    ],
    version = 2, exportSchema = true
)
abstract class HymnalDatabase : RoomDatabase() {

    abstract fun hymnalsDao(): HymnalsDao

    abstract fun hymnsDao(): HymnsDao

    abstract fun collectionsDao(): CollectionsDao
}
