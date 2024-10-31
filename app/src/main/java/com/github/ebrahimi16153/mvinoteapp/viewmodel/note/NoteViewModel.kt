package com.github.ebrahimi16153.mvinoteapp.viewmodel.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ebrahimi16153.noteapp.data.model.NoteModel
import com.github.ebrahimi16153.noteapp.data.repository.NoteRepository
import com.github.ebrahimi16153.noteapp.utils.Constant.EDUCATION
import com.github.ebrahimi16153.noteapp.utils.Constant.HEALTH
import com.github.ebrahimi16153.noteapp.utils.Constant.HIGH
import com.github.ebrahimi16153.noteapp.utils.Constant.HOME
import com.github.ebrahimi16153.noteapp.utils.Constant.LOW
import com.github.ebrahimi16153.noteapp.utils.Constant.MEDIUM
import com.github.ebrahimi16153.noteapp.utils.Constant.WORK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    val noteIntent = Channel<NoteIntent>()
   private val _noteState = MutableStateFlow<NoteState>(NoteState.Idle)
    val noteState: StateFlow<NoteState> get() = _noteState


    init {
        handelIntent()
    }



 private fun handelIntent() = viewModelScope.launch {
        noteIntent.consumeAsFlow().collect { itIntent ->
            when (itIntent) {
                is NoteIntent.SaveNote -> saveNote(entity = itIntent.note)
                is NoteIntent.SpinnersList -> fetchSpringList()
                is NoteIntent.NoteByID -> getNote(itIntent)
            }
        }
    }



    private fun saveNote(entity: NoteModel) = viewModelScope.launch {
        _noteState.value = try {
            NoteState.SaveNote(noteRepository.saveNote(entity))
        } catch (e: Exception) {
            NoteState.Error(message = e.message.toString())
        }
    }

    private fun fetchSpringList() = viewModelScope.launch {
        val categories = mutableListOf(WORK, HOME, EDUCATION, HOME, HEALTH)
        val priority = mutableListOf(HIGH, MEDIUM, LOW)
        _noteState.value = NoteState.Spinners(categories = categories, priorities = priority)
    }


    private fun getNote(itIntent: NoteIntent.NoteByID) = viewModelScope.launch{
        val id = itIntent.noteID
        noteRepository.getNote(id).collectLatest { itNote ->
            _noteState.value = NoteState.GetNote(itNote)
        }
    }

}