package hymnal.storage

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hymnal.storage.dao.CollectionsDao
import hymnal.storage.dao.HymnalsDao
import hymnal.storage.dao.HymnsDao
import hymnal.storage.entity.CollectionHymnCrossRefEntity
import hymnal.storage.entity.HymnCollectionEntity
import hymnal.storage.entity.HymnEntity
import hymnal.storage.entity.HymnalEntity

@Database(
    entities = [
        HymnalEntity::class,
        HymnEntity::class,
        HymnCollectionEntity::class,
        CollectionHymnCrossRefEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
    ]
)
internal abstract class HymnalDatabase : RoomDatabase() {

    abstract fun hymnalsDao(): HymnalsDao

    abstract fun hymnsDao(): HymnsDao

    abstract fun collectionsDao(): CollectionsDao

    companion object {
        private const val DATABASE_NAME = "hymnal.db"

        @Volatile
        private var INSTANCE: HymnalDatabase? = null

        fun getInstance(context: Context): HymnalDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): HymnalDatabase =
            Room.databaseBuilder(context, HymnalDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}