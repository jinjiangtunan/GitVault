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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddRepoViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val tokenRepository: TokenRepository,
    private val gitManager: GitManager
) : ViewModel() {

    val tokens = tokenRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class CloneState(val message: String, val progress: GitManager.Progress? = null)
    private val _cloneState = MutableStateFlow<CloneState?>(null)
    val cloneState: StateFlow<CloneState?> = _cloneState.asStateFlow()

    fun cloneRepo(name: String, url: String, tokenId: Long?) {
        if (name.isBlank() || url.isBlank()) return

        viewModelScope.launch {
            _cloneState.value = CloneState("正在克隆 $name…")

            val localPath = File(
                repoRepository.getAppRepoDir(),
                name.replace(Regex("[^a-zA-Z0-9\\-_.]"), "_")
            ).absolutePath

            val token = tokenId?.let { tokenRepository.getById(it) }

            val result = gitManager.cloneRepo(
                url = url,
                localPath = localPath,
                token = token?.token,
                onProgress = { progress ->
                    _cloneState.value = CloneState("克隆中…", progress)
                }
            )

            if (result.isSuccess) {
                repoRepository.insert(
                    RepoEntity(
                        name = name,
                        url = url,
                        tokenId = tokenId,
                        localPath = localPath,
                        lastPulledAt = System.currentTimeMillis()
                    )
                )
                _cloneState.value = CloneState("克隆完成")
                kotlinx.coroutines.delay(1000)
                _cloneState.value = null
            } else {
                _cloneState.value = CloneState("克隆失败: ${result.exceptionOrNull()?.message}")
                kotlinx.coroutines.delay(3000)
                _cloneState.value = null
            }
        }
    }
}
