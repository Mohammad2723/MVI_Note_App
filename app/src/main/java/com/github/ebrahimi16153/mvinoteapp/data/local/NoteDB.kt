package com.github.ebrahimi16153.mvinoteapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.ebrahimi16153.noteapp.data.model.NoteModel


@Database(entities = [NoteModel::class], version = 1, exportSchema = false)
abstract class NoteDB : RoomDatabase() {
    abstract val dao: NoteDao

}
