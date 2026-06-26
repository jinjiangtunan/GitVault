package com.gitvault.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitvault.app.data.model.RepoEntity
import com.gitvault.app.data.repository.RepoRepository
import com.gitvault.app.data.repository.TokenRepository
import com.gitvault.app.domain.GitManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoListViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val tokenRepository: TokenRepository,
    private val gitManager: GitManager
) : ViewModel() {

    val repos: StateFlow<List<RepoEntity>> = repoRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class PullState(val message: String, val progress: GitManager.Progress? = null)
    private val _pullState = MutableStateFlow<PullState?>(null)
    val pullState: StateFlow<PullState?> = _pullState.asStateFlow()

    fun pullRepo(repo: RepoEntity) {
        viewModelScope.launch {
            _pullState.value = PullState("正在拉取 ${repo.name}…")
            val token = repo.tokenId?.let { tokenRepository.getById(it) }

            val result = gitManager.pullRepo(
                localPath = repo.localPath,
                token = token?.token,
                onProgress = { progress ->
                    _pullState.value = PullState("拉取中…", progress)
                }
            )

            if (result.isSuccess) {
                repoRepository.update(repo.copy(lastPulledAt = System.currentTimeMillis()))
                _pullState.value = null
            } else {
                _pullState.value = PullState("拉取失败: ${result.exceptionOrNull()?.message}")
                kotlinx.coroutines.delay(2000)
                _pullState.value = null
            }
        }
    }

    fun deleteRepo(repo: RepoEntity) {
        viewModelScope.launch {
            // Delete local directory
            try {
                java.io.File(repo.localPath).deleteRecursively()
            } catch (_: Exception) {}
            repoRepository.delete(repo)
        }
    }
}
