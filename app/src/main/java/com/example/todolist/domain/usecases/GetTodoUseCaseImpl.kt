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
class GetTodoUseCaseImpl(
    private val todoRepository: TodoRepository,
    private val dispatcher: CoroutineDispatcher
): GetTodoUseCaseInterface, CoroutineScope {

    override suspend fun getAllTodo(): LiveData<List<Todo>> {
        return todoRepository.getAllTodo()
    }

    protected val jobTracker: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + jobTracker
}