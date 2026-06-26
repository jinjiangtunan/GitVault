package com.gitvault.app.di

import android.content.Context
import androidx.room.Room
import com.gitvault.app.data.db.AppDatabase
import com.gitvault.app.data.db.RepoDao
import com.gitvault.app.data.db.TokenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gitvault.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRepoDao(database: AppDatabase): RepoDao = database.repoDao()

    @Provides
    fun provideTokenDao(database: AppDatabase): TokenDao = database.tokenDao()
}
