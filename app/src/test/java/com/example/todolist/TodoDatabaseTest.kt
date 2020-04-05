package com.example.todolist

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.todolist.repository.database.TodoDatabase
import com.example.todolist.model.Todo
import com.example.todolist.repository.TodoRepository
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
 * リポジトリ用の単体テストクラスです
 */
@RunWith(RobolectricTestRunner::class)
class TodoDatabaseTest {
    private lateinit var repository: TodoRepository
    private lateinit var db: TodoDatabase
    private lateinit var context: Context

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TodoRepository(db.todoDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `write and get Todo Test ` () {
        val todo = Todo(1, "todo text", false)
        val inputCount = repository.insertTodo(todo)

        assertTrue(inputCount == 1.toLong())

        val resultLiveData = repository.getAllTodo()

        resultLiveData.test()
            .awaitValue()
            .assertHasValue()
            .assertValue(listOf(todo))
    }

    @Test
    fun `Insert, modify and get Todo Test` () {
        val todo = Todo(1, "todo text", false)
        repository.insertTodo(todo)

        todo.done = true
        todo.content = "Nothing"

        repository.updateTodo(todo)

        val resultLiveData = repository.getAllTodo()

        resultLiveData.test()
            .awaitValue()
            .assertHasValue()
            .assertValue(listOf(todo))
    }


    @Test
    fun `Insert, delete and get Todo Test` () {
        val todo = Todo(1, "todo text", false)
        repository.insertTodo(todo)
        repository.deleteTodo(todo)
        val resultLiveData = repository.getAllTodo()

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
        repository.insertTodo(todo)
        repository.insertTodo(todoCompleted)
        repository.insertTodo(todoCompleted2)
        repository.deleteAllCompleted()
        val resultLiveData = repository.getAllTodo()

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
        repository.insertTodo(todo)
        repository.insertTodo(todoCompleted)
        repository.insertTodo(todoCompleted2)
        repository.updateCompletion(1) // 1 = true

        val liveData: LiveData<List<Todo>> = repository.getAllTodo()
        val testObserver = TestObserver.test(liveData)

        // リストでは全アイテムのmDoneがぞれぞtrueになっていないことを確認
        assertTrue(testObserver.value().none{ !it.done })

    }


}