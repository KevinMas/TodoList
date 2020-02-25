package com.example.todolist.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.data.model.Todo
import com.example.todolist.presentation.adapter.TodoRecyclerAdapter
import com.example.todolist.presentation.viewmodel.TodoViewModel
import kotlinx.android.synthetic.main.todo_activity_main.*

class TodoActivity : AppCompatActivity() {

    private lateinit var mAdapter: TodoRecyclerAdapter
    private lateinit var mTodoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_main)

        // RecyclerViewを準備する
        todo_recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = TodoRecyclerAdapter(this,
            { todo, done -> mTodoViewModel.toggleDone(todo, done) },
            { todo -> mTodoViewModel.updateTodo(todo)},
            { todo -> mTodoViewModel.deleteTodo(todo)})

        todo_recyclerView.adapter = mAdapter

        todo_editText.afterTextChanged { content ->
            run {
                if (content.contains("\n")) {
                    val todo = Todo(0, content.replace("\n", ""), false)
                    mTodoViewModel.insertTodo(todo)
                    todo_editText.setText("")
                    todo_editText.clearFocus()
                }
            }
        }

        mTodoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        mTodoViewModel.todos.observe(this, Observer { todos ->
            todos?.let{
                mAdapter.setTodos(todos)
            }
        })

    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged(editable.toString())
        }
    })
}