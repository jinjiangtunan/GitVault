package com.gitvault.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gitvault.app.domain.GitManager
import com.gitvault.app.ui.theme.*

@Composable
fun LoadingDialog(
    title: String,
    message: String,
    progress: GitManager.Progress? = null,
    onDismiss: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            color = ObsidianSurface,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                CircularProgressIndicator(
                    color = AccentPrimary,
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (progress != null && progress.taskTitle.isNotEmpty()) {
                    Text(
                        text = progress.taskTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                if (progress != null && progress.totalWork > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { if (progress.totalWork > 0) progress.workDone.toFloat() / progress.totalWork else 0f },
                        modifier = Modifier.fillMaxWidth(),
                        color = AccentPrimary,
                        trackColor = ObsidianBorder
                    )
                }
            }
        }
    }
}
