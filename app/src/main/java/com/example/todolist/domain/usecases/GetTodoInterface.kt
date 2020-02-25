package com.example.todolist.domain.usecases

import androidx.lifecycle.LiveData
import com.example.todolist.data.model.Todo

/**
 * TODOアイテムを追加するユースケース
 */
interface GetTodoInterface {
    fun getAllTodo(): LiveData<List<Todo>>
}