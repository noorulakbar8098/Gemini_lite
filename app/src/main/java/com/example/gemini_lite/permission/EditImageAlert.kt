package com.example.gemini_lite.permission

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gemini_lite.chatScreen.ChatViewModel.ImageOption

@Composable
fun EditImageDialog(
    onOptionSelected: (ImageOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Choose Image Option") },
        text = {
            Column {
                Text(
                    text = "Camera",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(ImageOption.Camera) }
                        .padding(8.dp)
                )
                Text(
                    text = "Gallery",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(ImageOption.Gallery) }
                        .padding(8.dp)
                )
            }
        },
        confirmButton = { Text("Cancel", modifier = Modifier.clickable { onDismiss() }) }
    )
}
