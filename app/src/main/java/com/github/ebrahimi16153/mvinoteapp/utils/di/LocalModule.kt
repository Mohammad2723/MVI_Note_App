package com.github.ebrahimi16153.noteapp.utils.di

import android.content.Context
import androidx.room.Room
import com.github.ebrahimi16153.mvinoteapp.data.local.NoteDB
import com.github.ebrahimi16153.noteapp.data.model.NoteModel
import com.github.ebrahimi16153.noteapp.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) : NoteDB =
        Room.databaseBuilder(context, NoteDB::class.java, Constant.NOTE_DATABASE)
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()


    @Singleton
    @Provides
    fun provideDao(db: NoteDB) = db.dao

    @Singleton
    @Provides
    fun provideNoteEntity(): NoteModel = NoteModel()


}