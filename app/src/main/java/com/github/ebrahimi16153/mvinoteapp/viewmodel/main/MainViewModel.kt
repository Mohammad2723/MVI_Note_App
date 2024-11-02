package com.github.ebrahimi16153.mvinoteapp.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ebrahimi16153.noteapp.data.repository.MainRepository
import com.github.ebrahimi16153.noteapp.utils.Constant.ALL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    val intent = Channel<MainIntent>()
    private var _state = MutableStateFlow<MainState>(MainState.Empty)
    val state: StateFlow<MainState> get() = _state

    init {
        handelState()
    }

    private fun handelState() = viewModelScope.launch {
        intent.consumeAsFlow().collect { itIntent ->

            when (itIntent) {
                is MainIntent.NoteListAll -> getAllNotes()
                is MainIntent.NoteListByPriority -> getNotsByPriority(itIntent)
                is MainIntent.SearchNote -> getNotesBySearchQuery(itIntent)
                is MainIntent.DeleteNote -> deleteNote(itIntent)
            }
        }
    }



    private fun getAllNotes() = viewModelScope.launch {
        mainRepository.getNotes().collect { itNotes ->
            if (itNotes.isNotEmpty()) {
                _state.value = MainState.ListState(itNotes)
            } else {
                _state.value = MainState.Empty
            }
        }
    }


    private fun getNotsByPriority(intent: MainIntent.NoteListByPriority) = viewModelScope.launch {
        when (val priority = intent.priority) {
            ALL -> getAllNotes()
            else -> {
                mainRepository.filterByPriority(priority).collect { itNotes ->
                    _state.value = MainState.ListState(itNotes)
                }

            }
        }

    }

    private fun getNotesBySearchQuery(intent: MainIntent.SearchNote) = viewModelScope.launch {
        mainRepository.getSearchNote(intent.searchQuery).collect { itNotes ->
            if (itNotes.isNotEmpty())
                _state.value = MainState.ListState(notes = itNotes)
            else
                _state.value = MainState.Empty
        }
    }


    private fun deleteNote(intent: MainIntent.DeleteNote) = viewModelScope.launch {
        MainState.DeleteNote(mainRepository.delete(intent.note))
    }


}