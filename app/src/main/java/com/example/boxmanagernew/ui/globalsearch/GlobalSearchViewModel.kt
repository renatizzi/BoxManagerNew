package com.example.boxmanagernew.ui.globalsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GlobalSearchViewModel : ViewModel() {

    private val _messages =
        MutableLiveData<List<String>>(
            emptyList()
        )

    val messages:
            LiveData<List<String>> =
        _messages

    fun setMessages(
        messages: List<String>
    ) {

        _messages.value =
            messages
    }

    fun clear() {

        _messages.value =
            emptyList()
    }
}