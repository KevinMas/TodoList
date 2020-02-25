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

    fun insertTodo(todo: Todo): Long {
        return todoDao.insertTodo(todo)
    }

    fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }

    fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }

    // TODO insert, delete...

}