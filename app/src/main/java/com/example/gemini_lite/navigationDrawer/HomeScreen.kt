package com.example.gemini_lite.navigationDrawer

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.gemini_lite.R
import com.example.gemini_lite.chatScreen.ChatViewModel
import com.example.gemini_lite.chatScreen.ChatViewModel.ImageOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isCameraActionPending by remember { mutableStateOf(false) }
    var isGalleryActionPending by remember { mutableStateOf(false) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            capturedImageUri?.let { uri ->
                viewModel.setProfileImage(uri.toString())
                Log.d("CameraOption", "Image captured and sent to ViewModel")
            }
        } else {
            Log.e("CameraOption", "Camera capture failed or cancelled.")
        }
        capturedImageUri =  null
        isCameraActionPending = false
    }

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
                viewModel.setProfileImage(filePath)
            }
        } else {
            Log.e("GalleryLauncher", "No image selected.")
        }
        capturedImageUri =  null
        isGalleryActionPending = false
    }

    fun handleImageOption(option: ImageOption) {
        when (option) {
            ImageOption.Camera -> {
                if (!isCameraActionPending) {
                    isCameraActionPending = true
                    prepareCamera()
                }
            }
            ImageOption.Gallery -> {
                if (!isGalleryActionPending) {
                    isGalleryActionPending = true
                    galleryLauncher.launch("image/*")
                }
            }
        }
    }


    AppDrawer(
        viewModel = viewModel,
        onDestinationClicked = { destination ->
            if (destination == context.getString(R.string.logout)) {
                navController.navigate(context.getString(R.string.loginScreen)) {
                    popUpTo(context.getString(R.string.home_screen)) { inclusive = true }
                }
                viewModel.handleLogout(context)
                viewModel.clearChatHistory()
            }
        },
        onImageOptionSelected = { option ->
            handleImageOption(option)
        }
    )
}
