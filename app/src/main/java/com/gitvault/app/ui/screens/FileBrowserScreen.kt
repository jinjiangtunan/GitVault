package com.gitvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gitvault.app.ui.components.FileTreeItem
import com.gitvault.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    repoId: Long,
    onBack: () -> Unit,
    onOpenFile: (String) -> Unit,
    viewModel: FileBrowserViewModel = hiltViewModel()
) {
    val repoName by viewModel.repoName.collectAsState()
    val files by viewModel.files.collectAsState()

    LaunchedEffect(repoId) { viewModel.loadRepo(repoId) }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        repoName ?: "文件浏览",
                        color = TextPrimary,
                        maxLines = 1
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianSidebar),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = TextSecondary
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (files.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Outlined.FolderOpen,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("空目录", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(files) { node ->
                    FileTreeItem(
                        node = node,
                        onFileClick = onOpenFile
                    )
                }
            }
        }
    }
}
