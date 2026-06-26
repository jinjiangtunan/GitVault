package com.gitvault.app.data.db

import androidx.room.*
import com.gitvault.app.data.model.AccessTokenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM access_tokens ORDER BY createdAt DESC")
    fun getAll(): Flow<List<AccessTokenEntity>>

    @Query("SELECT * FROM access_tokens WHERE id = :id")
    suspend fun getById(id: Long): AccessTokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: AccessTokenEntity): Long

    @Delete
    suspend fun delete(token: AccessTokenEntity)

    @Query("DELETE FROM access_tokens WHERE id = :id")
    suspend fun deleteById(id: Long)
}
