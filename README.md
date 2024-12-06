Get started with the Gemini API
This repository contains a sample app demonstrating how the SDK can access and utilize the Gemini model for various use cases.

To try out the sample app you can directly import the project from Android Studio via File > New > Import Sample and searching for Generative AI Sample or follow these steps below:

Go to Google AI Studio.
Login with your Google account.
Create an API key. 
Check out this repository.
git clone [https://github.com/google/generative-ai-android](https://github.com/noorulakbar8098/Gemini_lite)
Open and build the sample app in the generativeai-android-sample folder of this repo.
Paste your API key into the apiKey property in the local.properties file.
Run the app
For detailed instructions, try the Android SDK tutorial on ai.google.dev.

Usage example
Add the dependency implementation("com.google.ai.client.generativeai:generativeai:<version>") to your Android project.

Initialize the model

val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-pro-latest",
    apiKey = BuildConfig.apiKey
)

Run a prompt.
val cookieImage: Bitmap = // ...
val inputContent = content() {
  image(cookieImage)
  text("Does this look store-bought or homemade?")
}

val response = generativeModel.generateContent(inputContent)
print(response.text)
For detailed instructions, you can find a quickstart for the Google AI client SDK for Android in the Google documentation.

This quickstart describes how to add your API key and the SDK's dependency to your app, initialize the model, and then call the API to access the model. It also describes some additional use cases and features, like streaming, counting tokens, and controlling responses.

Documentation
See the Gemini API [Cookbook ](https://github.com/google-gemini/cookbook)or [https://ai.google.dev/](https://ai.google.dev/) for complete documentation.
