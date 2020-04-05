package com.example.todolist.repository.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.model.Todo

/**
 * RoomにリクエストするDAOクラスです。
 */
@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_item_table ORDER BY ID DESC")
    fun getAllTodo(): LiveData<List<Todo>>

    @Insert
    fun insertTodo(todo: Todo): Long

    @Update
    fun updateTodo(todo: Todo)

    @Delete
    fun deleteTodo(todo: Todo)

    @Query("DELETE FROM todo_item_table WHERE done = 1")
    fun deleteAllCompleted()

    @Query("UPDATE todo_item_table SET done = :value")
    fun updateCompletion(value: Int)

}