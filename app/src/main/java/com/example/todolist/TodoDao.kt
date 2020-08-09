package com.example.todolist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao{
    @Insert
    suspend fun insert(todo: ToDoModel) : Long

    @Query("SELECT * FROM ToDoModel where isFinished==0" )
    fun getTask():LiveData<List<ToDoModel>>

    @Query("UPDATE ToDoModel SET isFinished =1 WHERE id=:uid")
    fun finishTask(uid:Long)

    @Query("DELETE FROM ToDoModel WHERE id=:uid")
    fun deleteTask(uid:Long)

}