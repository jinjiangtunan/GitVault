package com.gitvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gitvault.app.ui.components.LoadingDialog
import com.gitvault.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRepoScreen(
    onBack: () -> Unit,
    viewModel: AddRepoViewModel = hiltViewModel()
) {
    val tokens by viewModel.tokens.collectAsState()
    val cloneState by viewModel.cloneState.collectAsState()

    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var selectedTokenId by remember { mutableStateOf<Long?>(null) }

    cloneState?.let { state ->
        LoadingDialog(
            title = "正在克隆…",
            message = state.message,
            progress = state.progress
        )
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text("添加仓库", color = TextPrimary) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Repo name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("仓库名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors()
            )

            // Repo URL
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("仓库 URL (HTTPS)") },
                placeholder = { Text("https://github.com/user/repo.git", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors()
            )

            // Token selection (optional)
            if (tokens.isNotEmpty()) {
                Text("访问令牌（可选）", style = MaterialTheme.typography.labelLarge, color = TextSecondary)

                tokens.forEach { token ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedTokenId == token.id,
                            onClick = {
                                selectedTokenId = if (selectedTokenId == token.id) null else token.id
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AccentPrimary,
                                unselectedColor = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            token.label,
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clone button
            Button(
                onClick = {
                    viewModel.cloneRepo(name.trim(), url.trim(), selectedTokenId)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && url.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
            ) {
                Text("克隆仓库", color = TextPrimary)
            }

            // Error message
            cloneState?.message?.let { msg ->
                if (msg.contains("失败")) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(msg, color = StatusError, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = AccentPrimary,
    unfocusedBorderColor = ObsidianBorder,
    focusedLabelColor = AccentPrimary,
    unfocusedLabelColor = TextSecondary,
    cursorColor = AccentPrimary,
    focusedContainerColor = ObsidianSurface,
    unfocusedContainerColor = ObsidianSurface
)
