package com.example.todolist.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.todolist.data.database.TodoDatabase
import com.example.todolist.data.model.Todo
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TodoViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val todoRepository : TodoRepository
    val todos : LiveData<List<Todo>>

    init {
        val todoDao = TodoDatabase.getInstance(application).todoDao()
        todoRepository = TodoRepository(todoDao)
        todos = todoRepository.getAllTodo()
    }

    private val jobTracker: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Default + jobTracker

    fun insertTodo(todo: Todo) = launch {
        todoRepository.insertTodo(todo)
    }

    fun toggleDone(todo : Todo, checked : Boolean) {
        todo.mDone = checked
        updateTodo(todo)
    }

    fun updateTodo(todo: Todo) = launch{
        todoRepository.updateTodo(todo)
    }

    fun deleteTodo(todo: Todo) = launch {
        todoRepository.deleteTodo(todo)
    }

}