package com.example.todolist.domain.usecases

import androidx.lifecycle.LiveData
import com.example.todolist.data.model.Todo
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 *
 */
class InsertTodoImpl(
    private val todoRepository: TodoRepository
): InsertTodoInterface {

    override suspend fun insertTodo(todo: Todo): Long {
        return todoRepository.insertTodo(todo)
    }


}