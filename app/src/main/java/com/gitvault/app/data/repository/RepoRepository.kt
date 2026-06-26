package com.gitvault.app.data.repository

import android.content.Context
import com.gitvault.app.data.db.RepoDao
import com.gitvault.app.data.model.RepoEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repoDao: RepoDao
) {
    /**
     * Root directory for all cloned repos.
     * Android internal storage: /data/data/com.gitvault.app/files/repos/
     */
    fun getAppRepoDir(): File {
        val dir = File(context.filesDir, "repos")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getAll(): Flow<List<RepoEntity>> = repoDao.getAll()

    suspend fun getById(id: Long): RepoEntity? = repoDao.getById(id)

    suspend fun insert(repo: RepoEntity): Long = repoDao.insert(repo)

    suspend fun update(repo: RepoEntity) = repoDao.update(repo)

    suspend fun delete(repo: RepoEntity) = repoDao.delete(repo)

    suspend fun deleteById(id: Long) = repoDao.deleteById(id)
}
