package com.example.gemini_lite.common

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.gemini_lite.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GoogleSignInUtils {
    companion object {
        fun doGoogleSignIn(
            context: Context,
            scope: CoroutineScope,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            login: (String?, String?, String?) -> Unit // Updated to pass name and email
        ) {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }

            val credentialManager = CredentialManager.create(context)
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(context, request)
                    when (result.credential) {
                        is CustomCredential -> {
                            if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential =
                                    GoogleIdTokenCredential.createFrom(result.credential.data)
                                val googleTokenId = googleIdTokenCredential.idToken
                                val authCredential =
                                    GoogleAuthProvider.getCredential(googleTokenId, null)
                                val user =
                                    FirebaseAuth.getInstance().signInWithCredential(authCredential)
                                        .await().user
                                user?.let {
                                    if (!it.isAnonymous) { // Retrieve user's name and email
                                        val userName = it.displayName
                                        val userEmail = it.email
                                        val userProfilePic = it.photoUrl.toString()
                                        Log.d(
                                            "Google account",
                                            "username :$userName + email:$userEmail"
                                        )
                                        setLoggedIn(context, true)
                                        // Pass name and email to login callback
                                        login.invoke(userName, userEmail, userProfilePic)
                                    }
                                }
                            }
                        }

                        else -> {
                            // Handle other cases
                        }
                    }
                } catch (e: NoCredentialException) {
                    launcher?.launch(getIntent())
                } catch (e: GetCredentialException) {
                    e.printStackTrace()
                }
            }
        }

        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(context.getString(R.string.client_id))
                .build()
        }
    }
}

