package com.gitvault.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitvault.app.data.repository.RepoRepository
import com.gitvault.app.domain.FileExplorer
import com.gitvault.app.domain.FileNode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FileBrowserViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val fileExplorer: FileExplorer
) : ViewModel() {

    private val _repoName = MutableStateFlow<String?>(null)
    val repoName: StateFlow<String?> = _repoName.asStateFlow()

    private val _files = MutableStateFlow<List<FileNode>>(emptyList())
    val files: StateFlow<List<FileNode>> = _files.asStateFlow()

    fun loadRepo(repoId: Long) {
        viewModelScope.launch {
            val repo = repoRepository.getById(repoId) ?: return@launch
            _repoName.value = repo.name

            val nodes = withContext(Dispatchers.IO) {
                fileExplorer.listFiles(repo.localPath)
            }
            _files.value = nodes
        }
    }
}
