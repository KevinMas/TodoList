package com.example.todolist

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.todolist.data.database.TodoDao
import com.example.todolist.data.database.TodoDatabase
import com.example.todolist.data.model.Todo
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

/**
 * TODO Maybe test UseCase instead of db directly
 */
@RunWith(RobolectricTestRunner::class)
class TodoDatabaseTest {
    private lateinit var todoDao: TodoDao
    private lateinit var db: TodoDatabase
    lateinit var context: Context
    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        todoDao = db.todoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val todo = Todo(1, "todo text", false)
        val inputCount = todoDao.insertTodo(todo)
        val todoList = todoDao.getAllTodo()

        assertTrue(inputCount == 1.toLong())

        todoList.observeForever { todos ->
            assertTrue(todos.contains(todo))
        }

    }
}