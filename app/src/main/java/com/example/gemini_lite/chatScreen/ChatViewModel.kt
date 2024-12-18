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
import com.example.gemini_lite.R
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
    private var tts: TextToSpeech? = null
    private var isTTSInitialized = false
    private val _messageList = MutableStateFlow<List<Messages>>(emptyList())
    val messageList: StateFlow<List<Messages>> = _messageList.asStateFlow()
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
        modelName = context.getString(R.string.gemini_1_5_flash),
        apiKey = apiKey
    )

    private fun startNewConversation() {
        viewModelScope.launch {
            _sessionId.value = Random.nextLong(1, 100)
        }
    }


    fun setProfileImage(imageUrl: String) {
        _profileImageUri.value = imageUrl
        viewModelScope.launch {
            val profileId = profileData.value.id
            repository.updateProfileUrl(profileId, imageUrl)
        }
    }

    fun loadMessages() {
        viewModelScope.launch {
            getProfileDetails()
            _profileData.value = withContext(Dispatchers.IO) {
                repository.getProfileData()
            }

            _messageList.value = withContext(Dispatchers.IO) {
                repository.getALlMessages()
            }
        }
    }

    private suspend fun getProfileDetails() {
        try {
            val profileId = profileData.value.id
            if (profileId != -1) {
                val savedUrl = repository.getProfileUrl(profileId)
                _profileImageUri.value = savedUrl
            } else {
                val profileDetails = repository.getProfileData()
                if (profileDetails != null) {
                    _profileImageUri.value = profileDetails.profileUrl
                } else {
                    Log.e("Profile Details", "No profile details found in database.")
                }
            }
        } catch (e: Exception) {
            Log.e("Error", "Error fetching profile details: ${e.message}")
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
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
        val keywords = listOf("images", "photo", "picture", "images")
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


    suspend fun checkSessionExists(sessionId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            repository.isSessionPresent(sessionId)
        }
    }


    fun getAllSessions(onResult: (List<Long>) -> Unit) {
        viewModelScope.launch {
            val sessionIds = repository.getAllSessionIds()
            onResult(sessionIds)
        }
    }

    @RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(sessionId: Long, messageContent: String) {
        viewModelScope.launch {
            var parentId: Long? = null
            try {
                val isPresent = checkSessionExists(sessionId)
                if (!isPresent) {
                    clearChatHistory()
                    if (sessionId > 0) {
                        parentId = withContext(Dispatchers.IO) {
                            repository.getOrInsertChatHistory(sessionId)
                        }
                    }
                } else {
                    parentId = if (sessionId > 0) {
                        withContext(Dispatchers.IO) {
                            repository.getOrInsertChatHistory(sessionId)
                        }
                    } else {
                        withContext(Dispatchers.IO) {
                            repository.getLastUpdatedSessionId()
                        }
                    }
                }

                val userMessage = Messages(
                    sessionId = parentId,
                    messageModel = messageContent,
                    role = "user"
                )
                withContext(Dispatchers.IO) {
                    repository.insertMessage(userMessage)
                }
                addMessageToUI(userMessage)

                // Placeholder message
                val placeholderMessage = Messages(
                    sessionId = parentId,
                    messageModel = "Generating...",
                    role = "model"
                )
                withContext(Dispatchers.Main) {
                    addMessageToUI(placeholderMessage)
                }

                val chatResponse = withContext(Dispatchers.IO) {
                    generateModel.startChat(
                        history = _messageList.value.map {
                            content(it.role) { text(it.messageModel) }
                        }.toList()
                    )
                }

                val response = chatResponse.sendMessage(messageContent)
                val responseText = response.text?.replace("*", "") ?: "No response."
                if (isImageRequest(messageContent, responseText)) {
                    handleImageRequest(parentId, responseText)
                } else if (isVideoRequest(messageContent)) {
                    handleVideoRequest(parentId, messageContent)
                } else {
                    handleTextResponse(parentId, responseText)
                }

            } catch (e: Exception) {
                handleException(sessionId, e.message)
            }
        }
    }

    fun isVideoRequest(messageContent: String): Boolean {
        val generalKeywords = listOf(
            "video", "watch", "tutorial", "course", "youtube", "lecture", "learn", "education"
        )
        return generalKeywords.any { keyword ->
            messageContent.contains(keyword, ignoreCase = true)
        }
    }

    suspend fun handleVideoRequest(sessionId: Long?, messageContent: String) {
        val videoLink = getVideoReference(messageContent)
        val videoMessage = Messages(
            sessionId = sessionId,
            messageModel = videoLink ?: "Sorry, I couldn't find a video reference for your query.",
            role = "model"
        )
        removeLastPlaceholderFromUI()
        withContext(Dispatchers.IO) {
            repository.insertMessage(videoMessage)
        }
        withContext(Dispatchers.Main) {
            addMessageToUI(videoMessage)
        }
    }

    private fun getVideoReference(query: String): String? {
        val videoReferences = mapOf(
            "Jetpack Compose tutorial" to "https://www.youtube.com/watch?v=8q5qI3Ah9Us&list=PLRKyZvuMYSIO9sadcCwR0DR8UPi9bQlev",
            "Kotlin basics" to "https://www.youtube.com/watch?v=e7WIPwRd2s8&list=PLlxmoA0rQ-Lw5k_QCqVl3rsoJOnb_00UV",
            "MVVM architecture" to "https://www.youtube.com/watch?v=Xg_WMBV6cWM&list=PLUhfM8afLE_O6UgUgslEOXbeXC89j_aOM",
            "Android Development" to "https://www.youtube.com/watch?v=fis26HvvDII",
            "How to Make an Android App for Beginners" to "https://www.youtube.com/watch?v=EOfCEhWq8sg",
            "Android Performance Improvements" to "https://www.youtube.com/watch?v=epkAPnF5qrk&t=355s",
            "Java Programming Language" to "https://www.youtube.com/watch?v=eIrMbAQSU34"
        )

        val matchedScores = videoReferences.map { (key, url) ->
            val score = calculateMatchScore(query, key)
            key to Pair(url, score)
        }
        return matchedScores.maxByOrNull { it.second.second }?.second?.first
    }

    private fun calculateMatchScore(query: String, keyword: String): Int {
        return keyword.split(" ").sumOf { word ->
            if (query.contains(word, ignoreCase = true)) {
                10 * word.length // Assign higher scores to longer, more specific words
            } else 0
        }
    }

    private suspend fun handleTextResponse(sessionId: Long?, responseText: String) {
        val modelResponse = Messages(
            sessionId = sessionId,
            messageModel = responseText,
            role = "model"
        )
        removeLastPlaceholderFromUI()
        withContext(Dispatchers.IO) {
            repository.insertMessage(modelResponse)
        }
        withContext(Dispatchers.Main) {
            addMessageToUI(modelResponse)
        }
    }

    private suspend fun handleImageRequest(sessionId: Long?,responseText: String) {
        val imageUrl = fetchImageFromPexels(responseText)

        if (imageUrl != null) {
            val imageResponse = Messages(
                sessionId = sessionId,
                messageModel = imageUrl,
                role = "model"
            )
            removeLastPlaceholderFromUI()
            withContext(Dispatchers.IO) {
                repository.insertMessage(imageResponse)
            }
            withContext(Dispatchers.Main) {
                addMessageToUI(imageResponse)
            }
        } else {
            val fallbackResponse = Messages(
                sessionId = _sessionId.value,
                messageModel = "Sorry, I couldn't find an image for that request.",
                role = "model"
            )
            withContext(Dispatchers.IO) {
                repository.insertMessage(fallbackResponse)
            }
            withContext(Dispatchers.Main) {
                addMessageToUI(fallbackResponse)
            }
        }
    }

    private suspend fun handleException(sessionId: Long, message: String?) {
        val errorMessage = Messages(
            sessionId = sessionId,
            messageModel = "Error: ${message ?: "Unknown error"}",
            role = "model"
        )
        withContext(Dispatchers.IO) {
            repository.insertMessage(errorMessage)
        }
        withContext(Dispatchers.Main) {
            addMessageToUI(errorMessage)
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

    enum class ImageOption {
        Camera, Gallery
    }
}

