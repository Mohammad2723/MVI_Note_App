package com.github.ebrahimi16153.mvinoteapp.viewmodel.main

import com.github.ebrahimi16153.noteapp.data.model.NoteModel

sealed class MainIntent {

    data object NoteListAll:MainIntent()
    data class NoteListByPriority(val priority:String):MainIntent()
    data class DeleteNote(val note:NoteModel):MainIntent()
    data class SearchNote(val searchQuery:String):MainIntent()
}