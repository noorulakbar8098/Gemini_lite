package com.example.gemini_lite.chatScreen

import android.content.Context
import android.net.Uri
import android.os.Build.VERSION_CODES
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gemini_lite.DB.ChatHistory
import com.example.gemini_lite.DB.MessageRepository
import com.example.gemini_lite.DB.Messages
import com.example.gemini_lite.DB.ProfileData
import com.example.gemini_lite.common.clearLoginState
import com.example.gemini_lite.common.constants.apiKey
import com.example.gemini_lite.common.constants.pixelApiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.Locale
import kotlin.random.Random

class ChatViewModel(
    private val repository: MessageRepository, context: Context
) : ViewModel(), TextToSpeech.OnInitListener {
    private val _messageList = MutableStateFlow<List<Messages>>(emptyList())
    val messageList: StateFlow<List<Messages>> = _messageList.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTTSInitialized = false

    private val _chatHistories = MutableStateFlow<List<ChatHistory>>(emptyList())
    val chatHistories: StateFlow<List<ChatHistory>> = _chatHistories.asStateFlow()

    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages: StateFlow<List<Messages>> = _messages.asStateFlow()

    private val _profileData = MutableStateFlow(ProfileData())
    val profileData: StateFlow<ProfileData> = _profileData.asStateFlow()

    private val _sessionId = MutableStateFlow<Long>(-1)
    val sessionId: StateFlow<Long> = _sessionId.asStateFlow()

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri

    init {
        loadMessages()
        startNewConversation()
        tts = TextToSpeech(context, this)
    }

    private val generateModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    fun startNewConversation() {
        _sessionId.value = Random.nextLong(1000000000000, 9999999999999)
    }


    fun setProfileImage(ImageUrl: String) {
        _profileImageUri.value = ImageUrl
        Log.e("image", "ImageOption Uri: $profileImageUri")
        viewModelScope.launch {
            val profileId = profileData.value.id
            Log.e("image", "ImageOption Uri: $ImageUrl.toString()")
            repository.updateProfileUrl(profileId, ImageUrl)
        }
    }

    fun saveUriToFile(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            Log.e("SaveUriToFile", "Error saving image", e)
            null
        }
    }

    fun saveUserDetails(id: Int, name: String, email: String, photoUrl: String) {
        _profileData.value = ProfileData(id, name, email, photoUrl)
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                val profile = ProfileData(
                    id = id,
                    name = name,
                    email = email,
                    profileUrl = photoUrl
                )
                repository.insertProfileDetails(profile)
            }
        }
    }


    fun speakResponse(responseText: String, language: String) {
        if (isTTSInitialized) {
            try {
                val result = tts?.setLanguage(Locale(language))
                if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language $language not supported.")
                } else if (result == TextToSpeech.LANG_MISSING_DATA) {
                    Log.e("TTS", "Missing TTS data for language $language.")
                } else {
                    tts?.speak(responseText, TextToSpeech.QUEUE_FLUSH, null, null)
                    Log.d("TTS", "Speaking response: $responseText in language: $language")
                }
            } catch (e: Exception) {
                Log.e("TTS", "Error during speak: ${e.message}")
            }
        } else {
            Log.e("TTS", "TextToSpeech not initialized.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTTSInitialized = true
            Log.d("TTS", "TextToSpeech initialized successfully")
            logSupportedLanguages()
        } else {
            Log.e("TTS", "TextToSpeech initialization failed.")
        }
    }

    private fun logSupportedLanguages() {
        tts?.availableLanguages?.forEach {
            Log.d("TTS", "Supported language: ${it.language}")
        }
    }

    fun loadMessages() {
        viewModelScope.launch {
            _profileData.value = withContext(Dispatchers.IO) {
                repository.getProfileData()
            }

            _messageList.value = withContext(Dispatchers.IO) {
                repository.getALlMessages()
            }

            viewModelScope.launch {
                try {
                    val profileId = profileData.value.id // Default fallback ID

                    if (profileId != -1) {
                        // Fetch profile image if profile ID is valid
                        val savedUrl = repository.getProfileUrl(profileId)
                        Log.e("image from DB", "ImageOption Uri: $savedUrl")
                        _profileImageUri.value = savedUrl
                    } else {
                        // ID is not available; fetch all profile details
                        val profileDetails = repository.getProfileData()
                        if (profileDetails != null) {
                            Log.e("Profile Details", "Fetched all profile details: $profileDetails")
                            _profileImageUri.value = profileDetails.profileUrl
                        } else {
                            Log.e("Profile Details", "No profile details found in database.")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Error", "Error fetching profile details: ${e.message}")
                }
            }

        }
    }

    enum class ImageOption {
        Camera, Gallery
    }

    fun clearChatHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _messageList.value = emptyList()
        }
    }

    fun handleLogout(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            clearLoginState(context)
            repository.clearChatHistory()
            repository.clearMessages()
            repository.clearLoginCred()
        }
    }

    private fun isImageRequest(userInput: String, modelResponse: String): Boolean {
        val keywords = listOf("image", "photo", "picture", "send me", "show me")
        return keywords.any {
            userInput.contains(
                it,
                ignoreCase = true
            ) || modelResponse.contains(it, ignoreCase = true)
        }
    }

    private suspend fun fetchImageFromPexels(query: String): String? {
        val apiKey = pixelApiKey
        val url = "https://api.pexels.com/v1/search?query=$query&per_page=1"

        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", apiKey)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (responseBody != null) {
                    val json = JSONObject(responseBody)
                    val photos = json.getJSONArray("photos")
                    if (photos.length() > 0) {
                        val firstPhoto = photos.getJSONObject(0)
                        return@withContext firstPhoto.getJSONObject("src").getString("original")
                    }
                }
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    @RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(sessionId: Long, messageContent: String) {
        viewModelScope.launch {
            try {
                val parentId = repository.getOrInsertChatHistory(sessionId)
                val userMessage = Messages(
                    sessionId = parentId,
                    messageModel = messageContent,
                    role = "user"
                )
                withContext(Dispatchers.Main) {
                    repository.insertMessage(userMessage)
                    addMessageToUI(userMessage)
                }
                val placeholderMessage = Messages(
                    sessionId = sessionId,
                    messageModel = "Generating...",
                    role = "model"
                )
                withContext(Dispatchers.Main) {
                    addMessageToUI(placeholderMessage) // Placeholder for the model response
                }
                val chatResponse = withContext(Dispatchers.IO) {
                    generateModel.startChat(
                        history = _messageList.value.map {
                            content(it.role) { text(it.messageModel) }
                        }
                            .toList()
                    )
                }
                val response = withContext(Dispatchers.Main) {
                    chatResponse.sendMessage(messageContent)
                }
                val responseText = response.text?.replace("*", "") ?: "No response."

                if (isImageRequest(messageContent, responseText)) {
                    val imageUrl = fetchImageFromPexels(messageContent)
                    if (imageUrl != null) {
                        val imageResponse = Messages(
                            sessionId = _sessionId.value,
                            messageModel = imageUrl,
                            role = "model"
                        )
                        removeLastPlaceholderFromUI()
                        addMessageToUI(imageResponse)
                        repository.insertMessage(imageResponse)
                    } else {
                        val fallbackResponse = Messages(
                            sessionId = _sessionId.value,
                            messageModel = "Sorry, I couldn't find an image for that request.",
                            role = "model"
                        )
                        addMessageToUI(fallbackResponse)
                        repository.insertMessage(fallbackResponse)
                    }
                } else {
                    val modelResponse = Messages(
                        sessionId = sessionId,
                        messageModel = responseText,
                        role = "model"
                    )
                    Log.e("Response", "${response.text}")
                    withContext(Dispatchers.IO) {
                        repository.insertMessage(modelResponse)
                    }
                    withContext(Dispatchers.Main) {
                        removeLastPlaceholderFromUI()
                        addMessageToUI(modelResponse)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = Messages(
                    sessionId = sessionId,
                    messageModel = "Error: ${e.message}",
                    role = "model"
                )
                repository.insertMessage(errorMessage)
                withContext(Dispatchers.Main) {
                    removeLastPlaceholderFromUI()
                    addMessageToUI(errorMessage)
                }
            }
        }
    }

    private fun removeLastPlaceholderFromUI() {
        if (_messageList.value.isNotEmpty() && _messageList.value.last().role == "model" &&
            _messageList.value.last().messageModel == "Generating..."
        ) {
            _messageList.value = _messageList.value.dropLast(1)
        }
    }

    private fun addMessageToUI(message: Messages) {
        _messageList.value += message
    }
}