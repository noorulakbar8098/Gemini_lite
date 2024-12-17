package com.example.gemini_lite.navigationDrawer

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.gemini_lite.chatScreen.ChatViewModel
import com.example.gemini_lite.chatScreen.ChatViewModel.ImageOption

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.setProfileImage(capturedImageUri.toString())
            Log.d("CameraOption", "Image captured and sent to ViewModel")
        } else {
            Log.e("CameraOption", "Camera capture failed or cancelled.")
        }
    }

    // Generate temporary URI to store camera image
    fun createImageUri(context: Context): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "temp_image_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )
    }

    fun prepareCamera() {
        capturedImageUri = createImageUri(context)
        capturedImageUri?.let { cameraLauncher.launch(it) }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("GalleryLauncher", "Selected Image URI: $uri")
            val filePath = viewModel.saveUriToFile(context, uri)
            if (filePath != null) {
                viewModel.setProfileImage(filePath) // Save path
            }
        } else {
            Log.e("GalleryLauncher", "No image selected.")
        }
    }

    fun handleImageOption(option: ImageOption) {
        when (option) {
            ImageOption.Camera -> {
                Log.d("ImageOption", "Camera option selected")
                prepareCamera()
            }

            ImageOption.Gallery -> {
                Log.d("ImageOption", "Launching gallery...")
                galleryLauncher.launch("image/*")
            }
        }
    }

    AppDrawer(
        navController = navController,
        viewModel = viewModel,
        onDestinationClicked = { destination ->
            if (destination == "Logout") {
                navController.navigate("login_screen") {
                    popUpTo("home_screen") { inclusive = true }
                }
                viewModel.handleLogout(context)
                viewModel.clearChatHistory()
            } else if (destination == "New Chat") {
                viewModel.clearChatHistory()
            }
        },
        onImageOptionSelected = { option ->
            handleImageOption(option)
        }
    )
}
