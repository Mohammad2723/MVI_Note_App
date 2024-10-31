package com.github.ebrahimi16153.noteapp.data.repository

import com.github.ebrahimi16153.mvinoteapp.data.local.NoteDao
import com.github.ebrahimi16153.noteapp.data.model.NoteModel
import javax.inject.Inject

class MainRepository @Inject constructor(private val noteDao: NoteDao) {

    suspend fun delete(note: NoteModel) = noteDao.delete(note = note)
    fun getNotes() = noteDao.getAllNote()
    fun filterByPriority(priority: String) = noteDao.filterByPriority(priority = priority)
    fun getSearchNote(title: String) = noteDao.searchQuery(title = title)



}