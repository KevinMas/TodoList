package com.example.todolist.data.repository

import androidx.lifecycle.LiveData
import com.example.todolist.data.database.TodoDao
import com.example.todolist.data.model.Todo

/**
 *
 */
class TodoRepository(private val todoDao: TodoDao) {

    private val allTodo: LiveData<List<Todo>> = todoDao.getAllTodo()

    fun getAllTodo(): LiveData<List<Todo>> = allTodo

    // TODO insert, delete...

}