package com.gitvault.app.ui.screens

import android.content.Context
import android.text.Spanned
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitvault.app.data.repository.RepoRepository
import com.gitvault.app.domain.FileExplorer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MarkdownReaderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repoRepository: RepoRepository,
    private val fileExplorer: FileExplorer
) : ViewModel() {

    private val _content = MutableStateFlow<String?>(null)
    val content: StateFlow<String?> = _content.asStateFlow()

    private val _fileName = MutableStateFlow<String?>(null)
    val fileName: StateFlow<String?> = _fileName.asStateFlow()

    private val _renderedMarkdown = MutableStateFlow<Spanned?>(null)
    val renderedMarkdown: StateFlow<Spanned?> = _renderedMarkdown.asStateFlow()

    private val markwon: Markwon by lazy {
        Markwon.builder(context)
            .usePlugin(CoilImagesPlugin.create(context))
            .usePlugin(TablePlugin.create(context))
            .usePlugin(TaskListPlugin.create(context))
            .build()
    }

    fun loadFile(repoId: Long, filePath: String) {
        viewModelScope.launch {
            val repo = repoRepository.getById(repoId) ?: return@launch
            _fileName.value = java.io.File(filePath).name

            val text = withContext(Dispatchers.IO) {
                fileExplorer.readContent(java.io.File(repo.localPath, filePath).absolutePath)
            }
            _content.value = text

            if (text != null) {
                val spanned = withContext(Dispatchers.Default) {
                    markwon.toMarkdown(text)
                }
                _renderedMarkdown.value = spanned
            }
        }
    }
}
