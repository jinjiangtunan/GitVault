package com.gitvault.app.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps JGit operations — clone, pull — with progress callbacks.
 *
 * Authentication: HTTPS + Personal Access Token.
 * The token is passed as password; username is a dummy value (GitHub/Gitee ignore it).
 */
@Singleton
class GitManager @Inject constructor() {

    data class Progress(val taskTitle: String, val workDone: Int, val totalWork: Int)

    /**
     * Shallow-clone a remote repository to [localPath].
     *
     * @param url       Remote HTTPS URL (e.g. https://github.com/user/repo.git)
     * @param localPath Absolute path to clone into (directory must NOT exist yet)
     * @param token     Optional personal access token for private repos
     * @param onProgress Progress callback invoked on JGit's progress thread
     */
    suspend fun cloneRepo(
        url: String,
        localPath: String,
        token: String? = null,
        onProgress: ((Progress) -> Unit)? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val localDir = File(localPath)
            if (localDir.exists()) {
                localDir.deleteRecursively()
            }

            val cloneCommand: CloneCommand = Git.cloneRepository()
                .setURI(url)
                .setDirectory(localDir)
                .setDepth(1)                   // shallow clone
                .setCloneAllBranches(false)    // only default branch
                .setProgressMonitor(object : ProgressMonitor {
                    override fun start(totalTasks: Int) {}
                    override fun beginTask(title: String, totalWork: Int) {
                        onProgress?.invoke(Progress(title, 0, totalWork))
                    }
                    override fun update(completed: Int) {
                        onProgress?.invoke(Progress("", completed, -1))
                    }
                    override fun endTask() {}
                    override fun isCancelled(): Boolean = false
                })

            if (!token.isNullOrBlank()) {
                // GitHub/Gitee: username is ignored, password = token
                cloneCommand.setCredentialsProvider(
                    UsernamePasswordCredentialsProvider("oauth2", token)
                )
            }

            cloneCommand.call().use { /* close Git handle, directory stays */ }

            Result.success(Unit)
        } catch (e: TransportException) {
            Result.failure(Exception("无法连接到远程仓库: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("克隆失败: ${e.message}"))
        }
    }

    /**
     * Pull latest changes from the remote.
     * Assumes the repo was cloned from the same URL and credentials.
     */
    suspend fun pullRepo(
        localPath: String,
        token: String? = null,
        onProgress: ((Progress) -> Unit)? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val localDir = File(localPath)
            if (!localDir.exists() || !File(localDir, ".git").exists()) {
                return@withContext Result.failure(Exception("本地仓库不存在"))
            }

            Git.open(localDir).use { git ->
                val pullCommand: PullCommand = git.pull()
                    .setProgressMonitor(object : ProgressMonitor {
                        override fun start(totalTasks: Int) {}
                        override fun beginTask(title: String, totalWork: Int) {
                            onProgress?.invoke(Progress(title, 0, totalWork))
                        }
                        override fun update(completed: Int) {
                            onProgress?.invoke(Progress("", completed, -1))
                        }
                        override fun endTask() {}
                        override fun isCancelled(): Boolean = false
                    })

                if (!token.isNullOrBlank()) {
                    pullCommand.setCredentialsProvider(
                        UsernamePasswordCredentialsProvider("oauth2", token)
                    )
                }

                val result = pullCommand.call()
                if (result.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("拉取合并失败"))
                }
            }
        } catch (e: TransportException) {
            Result.failure(Exception("无法连接到远程仓库: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("拉取失败: ${e.message}"))
        }
    }
}
