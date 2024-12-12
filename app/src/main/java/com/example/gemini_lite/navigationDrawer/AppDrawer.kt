package com.example.gemini_lite.navigationDrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gemini_lite.chatScreen.ChatHomeScreen
import com.example.gemini_lite.chatScreen.ChatViewModel
import com.example.gemini_lite.chatScreen.UserDetails
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    navController: NavController,
    viewModel: ChatViewModel,
    onDestinationClicked: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val userDetail by viewModel.userDetails.collectAsState()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 100.dp)
                    .background(Color(0xFF181A1C))
            ) {
                DrawerHeader(userDetail)
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
fun DrawerHeader(userDetail: UserDetails?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF181A1C))
            .padding(top = 50.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = userDetail?.profileUrl,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))
        userDetail?.name?.let {
            Text(
                text = it,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        userDetail?.email?.let {
            Text(
                text = it,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun DrawerMenuItems(onItemClicked: (String) -> Unit) {
    val menuItems = listOf(
        "New Chat" to Icons.Default.Add,
        "Chat History" to Icons.Default.MailOutline,
        "Logout" to Filled.ExitToApp
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
            color = Color.White,
        )
    }
}

