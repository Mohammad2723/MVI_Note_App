package com.github.ebrahimi16153.mvinoteapp.viewmodel.main

import com.github.ebrahimi16153.noteapp.data.model.NoteModel

sealed class MainState {
    data object Idle:MainState()
    data class ListState(val notes:List<NoteModel>): MainState()
    data class DeleteNote(val unit:Unit):MainState()
    data object Empty:MainState()
}