package com.gitvault.app.data.db

import com.gitvault.app.data.model.AccessTokenEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRepository @Inject constructor(
    private val tokenDao: TokenDao
) {
    fun getAll(): Flow<List<AccessTokenEntity>> = tokenDao.getAll()

    suspend fun getById(id: Long): AccessTokenEntity? = tokenDao.getById(id)

    suspend fun insert(token: AccessTokenEntity): Long = tokenDao.insert(token)

    suspend fun delete(token: AccessTokenEntity) = tokenDao.delete(token)

    suspend fun deleteById(id: Long) = tokenDao.deleteById(id)
}
