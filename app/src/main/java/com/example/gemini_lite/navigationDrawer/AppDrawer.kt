package com.example.gemini_lite.navigationDrawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gemini_lite.R
import com.example.gemini_lite.chatScreen.ChatHomeScreen
import com.example.gemini_lite.chatScreen.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(viewModel: ChatViewModel, onDestinationClicked: @Composable (String) -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 100.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                DrawerHeader()
                DrawerMenuItems { destination ->
                    coroutineScope.launch {
                        drawerState.close()
                        onDestinationClicked(destination)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        content = {
            ChatHomeScreen(viewModel, openDrawer = {
                coroutineScope.launch {
                    drawerState.open()
                }
            })
        }
    )
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(top =  50.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.gemini_bg2),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "John Doe",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "johndoe@example.com",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
fun DrawerMenuItems(onItemClicked: (String) -> Unit) {
    val menuItems = listOf(
        "New Chat" to Icons.Default.Add,
        "Chat History" to Icons.Default.MailOutline,
        "Logout" to Icons.Default.ExitToApp
    )

    Column {
        menuItems.forEach { (label, icon) ->
            DrawerMenuItem(label = label, icon = icon) {
                onItemClicked(label)
            }
        }
    }
}

@Composable
fun DrawerMenuItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

