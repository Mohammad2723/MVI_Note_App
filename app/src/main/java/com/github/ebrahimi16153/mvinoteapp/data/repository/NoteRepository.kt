package com.github.ebrahimi16153.noteapp.data.repository

import com.github.ebrahimi16153.mvinoteapp.data.local.NoteDao
import com.github.ebrahimi16153.noteapp.data.model.NoteModel
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {

    suspend fun delete(note: NoteModel) = noteDao.delete(note = note)
    suspend fun saveNote(note: NoteModel) = noteDao.insert(note = note)
    suspend fun updateNote(note: NoteModel) = noteDao.update(note= note)
    fun getNote(id: Int) = noteDao.getNoteById(id = id)


}