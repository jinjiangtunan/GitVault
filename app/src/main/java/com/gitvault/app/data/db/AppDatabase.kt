package com.gitvault.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gitvault.app.data.model.AccessTokenEntity
import com.gitvault.app.data.model.RepoEntity

@Database(
    entities = [RepoEntity::class, AccessTokenEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun tokenDao(): TokenDao
}
