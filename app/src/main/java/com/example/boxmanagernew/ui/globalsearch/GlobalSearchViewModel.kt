package com.example.boxmanagernew.ui.globalsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxmanagernew.domain.search.model.SearchMessage

class GlobalSearchViewModel : ViewModel() {

    private val _messages =
        MutableLiveData<List<SearchMessage>>(
            emptyList()
        )

    val messages:
            LiveData<List<SearchMessage>> =
        _messages

    fun setMessages(
        messages: List<SearchMessage>
    ) {

        _messages.value =
            messages
    }

    fun addMessage(
        message: SearchMessage
    ) {

        val current =
            _messages.value
                ?.toMutableList()
                ?: mutableListOf()

        current.add(
            message
        )

        _messages.value =
            current
    }

    fun clear() {

        _messages.value =
            emptyList()
    }
}