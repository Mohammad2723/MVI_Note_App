package com.github.ebrahimi16153.mvinoteapp.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ebrahimi16153.mvinoteapp.viewmodel.note.NoteState
import com.github.ebrahimi16153.noteapp.data.model.NoteModel
import com.github.ebrahimi16153.noteapp.data.repository.MainRepository
import com.github.ebrahimi16153.noteapp.utils.Constant.ALL
import com.github.ebrahimi16153.noteapp.utils.Constant.HIGH
import com.github.ebrahimi16153.noteapp.utils.Constant.LOW
import com.github.ebrahimi16153.noteapp.utils.Constant.MEDIUM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    val intent = Channel<MainIntent>()
    private var _state = MutableStateFlow<MainState>(MainState.Idle)
    val state get() = _state

    init {
        handelState()
    }

    private fun handelState() = viewModelScope.launch {
        intent.consumeAsFlow().collect { itIntent ->

            when (itIntent) {
                MainIntent.NoteListAll -> fillList(ALL)
                MainIntent.NoteListHigh -> fillList(HIGH)
                MainIntent.NoteListLow -> fillList(LOW)
                MainIntent.NoteListMedium -> fillList(MEDIUM)
                is MainIntent.SearchNote -> fillSearchQuery(itIntent)
                is MainIntent.DeleteNote -> deleteNote(itIntent)
            }
        }
    }




    private fun fillList(priority: String) = viewModelScope.launch {
        when (priority) {
            ALL -> {
                mainRepository.getNotes().collect { itNotes ->
                    if (itNotes.isNotEmpty())
                        _state.value = MainState.ListState(notes = itNotes)
                    else
                        _state.value = MainState.Empty

                }
            }

            HIGH -> {
                mainRepository.filterByPriority(HIGH).collect { itNotes ->
                    if (itNotes.isNotEmpty())
                        _state.value = MainState.ListState(notes = itNotes)
                    else
                        _state.value = MainState.Empty
                }
            }

            MEDIUM -> {
                mainRepository.filterByPriority(MEDIUM).collect { itNotes ->
                    if (itNotes.isNotEmpty())
                        _state.value = MainState.ListState(notes = itNotes)
                    else
                        _state.value = MainState.Empty
                }
            }

            LOW -> {
                mainRepository.filterByPriority(LOW).collect { itNotes ->
                    if (itNotes.isNotEmpty())
                        _state.value = MainState.ListState(notes = itNotes)
                    else
                        _state.value = MainState.Empty
                }
            }

        }
    }

    private fun fillSearchQuery(intent: MainIntent.SearchNote) = viewModelScope.launch {
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