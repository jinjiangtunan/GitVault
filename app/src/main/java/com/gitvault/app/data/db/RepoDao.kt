package com.gitvault.app.data.db

import androidx.room.*
import com.gitvault.app.data.model.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repos ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repos WHERE id = :id")
    suspend fun getById(id: Long): RepoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repo: RepoEntity): Long

    @Update
    suspend fun update(repo: RepoEntity)

    @Delete
    suspend fun delete(repo: RepoEntity)

    @Query("DELETE FROM repos WHERE id = :id")
    suspend fun deleteById(id: Long)
}
