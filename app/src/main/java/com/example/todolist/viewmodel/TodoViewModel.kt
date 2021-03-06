package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.todolist.repository.database.TodoDatabase
import com.example.todolist.model.Todo
import com.example.todolist.repository.TodoRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * TodoビューをDATAレイアと繋ぐためのViewModelクラスです。
 */
class TodoViewModel(application: Application) :
    AndroidViewModel(application),
    CoroutineScope {

    // LiveData情報
    val todos : LiveData<List<Todo>>

    private val todoRepository : TodoRepository

    // Coroutine用の準備
    private val jobTracker: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + jobTracker

    init {
        // レポジトリを準備する
        val todoDao = TodoDatabase.getInstance(application).todoDao()
        todoRepository = TodoRepository(todoDao)
        todos = todoRepository.getAllTodo()
    }

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
        todo.done = checked
        updateTodo(todo)
    }

    /**
     * TODOアイテムをアップデートする関数です
     */
    fun updateTodo(todo: Todo) = launch {
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