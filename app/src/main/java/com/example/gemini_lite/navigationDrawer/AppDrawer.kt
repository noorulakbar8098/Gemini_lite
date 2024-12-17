package com.example.gemini_lite.navigationDrawer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gemini_lite.DB.ProfileData
import com.example.gemini_lite.chatScreen.ChatHomeScreen
import com.example.gemini_lite.chatScreen.ChatViewModel
import com.example.gemini_lite.chatScreen.ChatViewModel.ImageOption
import com.example.gemini_lite.permission.RequestCameraAndGalleryPermissions
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    navController: NavController,
    viewModel: ChatViewModel,
    onDestinationClicked: (String) -> Unit,
    onImageOptionSelected: (ImageOption) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val userDetail by viewModel.profileData.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    Log.e("image", "ImageOption Uri: $profileImageUri")
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 100.dp)
                    .background(Color(0xFF181A1C))
            ) {
                DrawerHeader(
                    userDetail,
                    profileImageUri,
                    onImageOptionSelected = { option ->
                        onImageOptionSelected(option)
                    })
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
fun DrawerHeader(
    userDetail: ProfileData?,
    profilePic: String?,
    onImageOptionSelected: (ImageOption) -> Unit
) {
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showPermissionAlert by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<ImageOption?>(null) }
    Log.e("image", "ImageOption Uri: $profilePic")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF181A1C))
            .padding(top = 50.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .size(100.dp)
            .clickable { showOptionsDialog = true }) {
            AsyncImage(
                model = profilePic ?: userDetail?.profileUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )

            Icon(
                imageVector = Icons.Default.Edit, // Use your preferred edit icon
                contentDescription = "Edit Profile",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp) // Adjust icon size
                    .background(Color(0xFF303030), CircleShape)
                    .padding(4.dp) // Add padding inside the background circle
            )
        }

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

    if (showOptionsDialog) {
        EditImageDialog(
            onOptionSelected = { option ->
                selectedOption = option
                showOptionsDialog = false
                showPermissionAlert = true
                onImageOptionSelected(option)
            },
            onDismiss = { showOptionsDialog = false }
        )
    }



    if (showPermissionAlert) {
        RequestCameraAndGalleryPermissions(
            onPermissionsGranted = {
                showPermissionAlert = false
                selectedOption?.let { onImageOptionSelected(it) }
            },
            onPermissionsDenied = { showPermissionAlert = false }
        )
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

@Composable
fun EditImageDialog(
    onOptionSelected: (ImageOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onOptionSelected(ImageOption.Camera) }) {
                Text("Camera")
            }
        },
        dismissButton = {
            Button(onClick = { onOptionSelected(ImageOption.Gallery) }) {
                Text("Gallery")
            }
        },
        title = { Text("Choose Option") },
        text = { Text("Select an option to edit your profile image.") }
    )
}


