package com.tinashe.hymnal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tinashe.hymnal.data.db.dao.CollectionsDao
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.HymnCollection
import com.tinashe.hymnal.data.model.Hymnal

@Database(
    entities = [
        Hymnal::class,
        Hymn::class,
        HymnCollection::class
    ],
    version = 1, exportSchema = true
)
@TypeConverters(DataTypeConverters::class)
abstract class HymnalDatabase : RoomDatabase() {

    abstract fun hymnalsDao(): HymnalsDao

    abstract fun hymnsDao(): HymnsDao

    abstract fun collectionsDao(): CollectionsDao
}
