package com.gitvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gitvault.app.data.model.RepoEntity
import com.gitvault.app.ui.components.LoadingDialog
import com.gitvault.app.ui.components.RepoCard
import com.gitvault.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoListScreen(
    onAddRepo: () -> Unit,
    onOpenRepo: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: RepoListViewModel = hiltViewModel()
) {
    val repos by viewModel.repos.collectAsState()
    val pullState by viewModel.pullState.collectAsState()

    // Pull progress dialog
    pullState?.let { state ->
        LoadingDialog(
            title = "正在拉取…",
            message = state.message,
            progress = state.progress
        )
    }

    // Delete confirmation
    var repoToDelete by remember { mutableStateOf<RepoEntity?>(null) }
    repoToDelete?.let { repo ->
        AlertDialog(
            onDismissRequest = { repoToDelete = null },
            containerColor = ObsidianSurface,
            title = { Text("删除仓库", color = TextPrimary) },
            text = { Text("确定要删除「${repo.name}」的本地副本吗？", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRepo(repo)
                    repoToDelete = null
                }) {
                    Text("删除", color = StatusError)
                }
            },
            dismissButton = {
                TextButton(onClick = { repoToDelete = null }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text("GitVault", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianSidebar),
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "设置",
                            tint = TextSecondary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRepo,
                containerColor = AccentPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "添加仓库", tint = TextPrimary)
            }
        }
    ) { padding ->
        if (repos.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Outlined.FolderOpen,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "还没有仓库",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "点击右下角 + 添加你的第一个 Git 仓库",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(repos, key = { it.id }) { repo ->
                    RepoCard(
                        repo = repo,
                        onClick = { onOpenRepo(repo.id) },
                        onPull = { viewModel.pullRepo(repo) },
                        onDelete = { repoToDelete = repo }
                    )
                }
            }
        }
    }
}
