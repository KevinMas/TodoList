package com.example.todolist.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.data.model.Todo

/**
 *
 */
@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_item_table")
    fun getAllTodo(): LiveData<List<Todo>>

    @Insert
    fun insertTodo(todo: Todo): Long

    @Update
    fun updateTodo(todo: Todo)

    @Delete
    fun deleteTodo(todo: Todo)
}