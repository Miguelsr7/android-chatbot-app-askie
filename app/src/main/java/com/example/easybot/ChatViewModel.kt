package com.example.easybot

import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    private val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constants.apiKey
    )


    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content (it.role){
                            text(it.message)
                        }
                    }.toList()
                )
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("...", "model"))

                val response = chat.sendMessage(question)
                messageList.removeLastOrNull()
                messageList.add(
                    MessageModel(response.text.orEmpty(), "model")
                )
            } catch (e: Exception) {
                messageList.removeLastOrNull()
                messageList.add(
                    MessageModel("Something went wrong. Please try again.", "model")
                )
            }
        }
    }
}
