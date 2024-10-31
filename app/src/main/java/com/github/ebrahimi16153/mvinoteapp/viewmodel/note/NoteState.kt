package com.github.ebrahimi16153.mvinoteapp.viewmodel.note

import com.github.ebrahimi16153.noteapp.data.model.NoteModel

sealed class NoteState {
     data object Idle:NoteState()
    data class Spinners(val categories: MutableList<String>, val priorities: MutableList<String>) : NoteState()
    data class Error(val message: String) : NoteState()
    data class SaveNote(val unit:Unit) : NoteState()
    data class GetNote(val note:NoteModel):NoteState()

}