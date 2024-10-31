package com.github.ebrahimi16153.mvinoteapp.viewmodel.note

import com.github.ebrahimi16153.noteapp.data.model.NoteModel

sealed class NoteIntent {

    data object SpinnersList : NoteIntent()
    data class NoteByID(val noteID:Int):NoteIntent()
    data class  SaveNote(val note:NoteModel):NoteIntent()

}