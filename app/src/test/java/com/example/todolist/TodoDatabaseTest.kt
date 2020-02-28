package com.example.todolist

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.todolist.data.database.TodoDao
import com.example.todolist.data.database.TodoDatabase
import com.example.todolist.data.model.Todo
import com.jraska.livedata.TestObserver
import com.jraska.livedata.test
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


/**
 * TODO Maybe test UseCase instead of db directly
 */
@RunWith(RobolectricTestRunner::class)
class TodoDatabaseTest {
    private lateinit var todoDao: TodoDao
    private lateinit var db: TodoDatabase
    lateinit var context: Context

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        todoDao = db.todoDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `write and get Todo Test ` () {
        val todo = Todo(1, "todo text", false)
        val inputCount = todoDao.insertTodo(todo)

        assertTrue(inputCount == 1.toLong())

        val resultLiveData = todoDao.getAllTodo()

        resultLiveData.test()
            .awaitValue()
            .assertHasValue()
            .assertValue(listOf(todo))
    }

    @Test
    fun `Insert, modify and get Todo Test` () {
        val todo = Todo(1, "todo text", false)
        todoDao.insertTodo(todo)

        todo.mDone = true
        todo.mContent = "Nothing"

        todoDao.updateTodo(todo)

        val resultLiveData = todoDao.getAllTodo()

        resultLiveData.test()
            .awaitValue()
            .assertHasValue()
            .assertValue(listOf(todo))
    }


    @Test
    fun `Insert, delete and get Todo Test` () {
        val todo = Todo(1, "todo text", false)
        todoDao.insertTodo(todo)
        todoDao.deleteTodo(todo)
        val resultLiveData = todoDao.getAllTodo()

        resultLiveData.test()
            .awaitValue()
            .assertHasValue()
            .assertValue(listOf())
    }

    @Test
    fun `Insert, delete all completed todo and get all Test` () {
        val todo = Todo(1, "todo text", false)
        val todoCompleted = Todo(2, "shopping", true)
        val todoCompleted2 = Todo(3, "dentist", true)
        todoDao.insertTodo(todo)
        todoDao.insertTodo(todoCompleted)
        todoDao.insertTodo(todoCompleted2)
        todoDao.deleteAllCompleted()
        val resultLiveData = todoDao.getAllTodo()

        resultLiveData.test()
            .awaitValue()
            .assertHasValue()
            .assertValue(listOf(todo))
    }

    @Test
    fun `Insert, complete all and get all Test` () {
        val todo = Todo(1, "todo text", false)
        val todoCompleted = Todo(2, "shopping", false)
        val todoCompleted2 = Todo(3, "dentist", true)
        todoDao.insertTodo(todo)
        todoDao.insertTodo(todoCompleted)
        todoDao.insertTodo(todoCompleted2)
        todoDao.updateCompletion(1) // 1 = true

        val liveData: LiveData<List<Todo>> = todoDao.getAllTodo()
        val testObserver = TestObserver.test(liveData)

        // リストでは全アイテムのmDoneがぞれぞtrueになっていないことを確認
        assertTrue(testObserver.value().none{ !it.mDone })

    }


}