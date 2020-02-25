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
class GetTodoImpl(
    private val todoRepository: TodoRepository
): GetTodoInterface {

    override fun getAllTodo(): LiveData<List<Todo>> {
        return todoRepository.getAllTodo()
    }
}