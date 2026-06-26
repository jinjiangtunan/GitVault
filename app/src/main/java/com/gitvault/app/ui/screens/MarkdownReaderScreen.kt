package com.gitvault.app.ui.screens

import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.gitvault.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownReaderScreen(
    repoId: Long,
    filePath: String,
    onBack: () -> Unit,
    viewModel: MarkdownReaderViewModel = hiltViewModel()
) {
    val content by viewModel.content.collectAsState()
    val fileName by viewModel.fileName.collectAsState()
    val renderedMarkdown by viewModel.renderedMarkdown.collectAsState()

    LaunchedEffect(repoId, filePath) {
        viewModel.loadFile(repoId, filePath)
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        fileName ?: "阅读",
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
        if (renderedMarkdown == null && content != null) {
            // Content loaded, still parsing markdown
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AccentPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("渲染中…", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else if (content == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentPrimary)
            }
        } else {
            // Use AndroidView to host Markwon-rendered TextView
            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        setTextColor(android.graphics.Color.parseColor("#D4D4D4"))
                        setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
                        textSize = 15f
                        setLineSpacing(4f, 1.2f)
                        setPadding(48, 32, 48, 64)
                    }
                },
                update = { textView ->
                    textView.text = renderedMarkdown
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
