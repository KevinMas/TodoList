package com.example.todolist.domain.usecases

import com.example.todolist.data.model.Todo

interface InsertTodoInterface {
    suspend fun insertTodo(todo: Todo): Long
}