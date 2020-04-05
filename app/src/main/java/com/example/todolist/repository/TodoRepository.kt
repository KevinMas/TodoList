package com.example.todolist.repository

import androidx.lifecycle.LiveData
import com.example.todolist.repository.database.TodoDao
import com.example.todolist.model.Todo

/**
 * レポジトリクラスです。ViewModelと繋がって、
 * 貰う情報をデータレイアに更新したり、ViewModelにデータを送ったりします。
 * ここはAPIかロカルからデータを取得したり、更新したりする判断場所ですが、
 * 今回はロカルだけで十分なので、判断は不要です。
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

    fun updateCompletion(done: Int) {
        todoDao.updateCompletion(done)
    }

    fun deleteAllCompleted() {
        todoDao.deleteAllCompleted()
    }

}