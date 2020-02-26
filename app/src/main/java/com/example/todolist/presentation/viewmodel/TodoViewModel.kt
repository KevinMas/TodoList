package com.example.todolist.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.todolist.data.database.TodoDatabase
import com.example.todolist.data.model.Todo
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * TodoビューをDATAレイアと栂ぐためのViewModelクラスです。
 */
class TodoViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val todoRepository : TodoRepository
    // LiveData情報
    val todos : LiveData<List<Todo>>

    // Coroutineようの準備
    private val jobTracker: Job = Job()

    init {
        // レポジトリを準備する
        val todoDao = TodoDatabase.getInstance(application).todoDao()
        todoRepository = TodoRepository(todoDao)
        todos = todoRepository.getAllTodo()
    }

    override val coroutineContext: CoroutineContext get() = Dispatchers.Default + jobTracker

    /**
     * TODOアイテムを登録する関数です
     */
    fun insertTodo(todo: Todo) = launch {
        todoRepository.insertTodo(todo)
    }

    /**
     * TODOアイテムを完了にアップデートする関数です
     */
    fun toggleDone(todo : Todo, checked : Boolean) {
        todo.mDone = checked
        updateTodo(todo)
    }

    /**
     * TODOアイテムをアップデートする関数です
     */
    fun updateTodo(todo: Todo) = launch{
        todoRepository.updateTodo(todo)
    }

    /**
     * TODOアイテムを削除する関数です
     */
    fun deleteTodo(todo: Todo) = launch {
        todoRepository.deleteTodo(todo)
    }

    /**
     * 全TODOアイテムを完了にしたり無完了にしたりする更新関数です
     */
    fun updateCompletion(done: Int) = launch {
        todoRepository.updateCompletion(done)
    }

    /**
     * 全TODOアイテムを削除する関数です
     */
    fun deleteAllCompleted() = launch {
        todoRepository.deleteAllCompleted()
    }

}