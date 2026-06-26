package com.gitvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gitvault.app.data.model.AccessTokenEntity
import com.gitvault.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val tokens by viewModel.tokens.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("") }
    var newToken by remember { mutableStateOf("") }

    // Add token dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = ObsidianSurface,
            title = { Text("添加访问令牌", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { newLabel = it },
                        label = { Text("标签（如 GitHub、Gitee）") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedLabelColor = AccentPrimary,
                            unfocusedLabelColor = TextSecondary,
                            cursorColor = AccentPrimary,
                            focusedContainerColor = ObsidianSurfaceVariant,
                            unfocusedContainerColor = ObsidianSurfaceVariant
                        )
                    )
                    OutlinedTextField(
                        value = newToken,
                        onValueChange = { newToken = it },
                        label = { Text("Access Token") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedLabelColor = AccentPrimary,
                            unfocusedLabelColor = TextSecondary,
                            cursorColor = AccentPrimary,
                            focusedContainerColor = ObsidianSurfaceVariant,
                            unfocusedContainerColor = ObsidianSurfaceVariant
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newLabel.isNotBlank() && newToken.isNotBlank()) {
                            viewModel.addToken(newLabel.trim(), newToken.trim())
                            newLabel = ""
                            newToken = ""
                            showAddDialog = false
                        }
                    }
                ) { Text("保存", color = AccentPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text("访问令牌管理", color = TextPrimary) },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "添加令牌", tint = TextPrimary)
            }
        }
    ) { padding ->
        if (tokens.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Outlined.Key,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "还没有访问令牌",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "在设置中添加 GitHub / Gitee 的 Personal Access Token\n用于访问私有仓库",
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tokens, key = { it.id }) { token ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Key,
                                contentDescription = null,
                                tint = AccentPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(token.label, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "••••${token.token.takeLast(4)}",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { viewModel.deleteToken(token) }) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = "删除",
                                    tint = StatusError
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
