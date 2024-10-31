package com.github.ebrahimi16153.mvinoteapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.ebrahimi16153.noteapp.utils.Constant
import com.github.ebrahimi16153.noteapp.data.model.NoteModel
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteModel)

    @Update
    suspend fun update(note: NoteModel)

    @Delete
    suspend fun delete(note: NoteModel)

    @Query("SELECT * FROM ${Constant.NOTE_TABLE}")
    fun getAllNote():Flow<MutableList<NoteModel>>

    @Query("DELETE FROM ${Constant.NOTE_TABLE}")
    fun deleteAll()


    @Query("SELECT * FROM ${Constant.NOTE_TABLE} WHERE id==:id")
    fun getNoteById(id:Int):Flow<NoteModel>

    //filter
    @Query("SELECT * FROM ${Constant.NOTE_TABLE} WHERE priority == :priority ")
    fun filterByPriority(priority:String): Flow<MutableList<NoteModel>>


    //search
    @Query("SELECT * FROM ${Constant.NOTE_TABLE} WHERE title LIKE '%' || :title || '%'")
    fun searchQuery(title:String):Flow<MutableList<NoteModel>>


}