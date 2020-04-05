package com.example.todolist.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.model.Todo
import com.example.todolist.ui.adapter.TodoRecyclerAdapter
import com.example.todolist.viewmodel.TodoViewModel
import kotlinx.android.synthetic.main.todo_activity_main.*

/**
 * TODOアプリのアクティビティクラスです。
 */
class TodoActivity : AppCompatActivity() {

    private var currentFilter : TodoRecyclerAdapter.TodoFilter? = TodoRecyclerAdapter.TodoFilter.ALL

    private lateinit var todoRecyclerAdapter: TodoRecyclerAdapter
    private lateinit var todoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_main)

        // RecyclerViewを準備する
        todo_recyclerView.layoutManager = LinearLayoutManager(this)
        // アダプター作成
        todoRecyclerAdapter = TodoRecyclerAdapter(this,
            { todo, done -> todoViewModel.toggleDone(todo, done) },
            { todo -> todoViewModel.updateTodo(todo) },
            { todo -> todoViewModel.deleteTodo(todo) }
        )
        // RecyclerViewにアダプターを設定
        todo_recyclerView.adapter = todoRecyclerAdapter

        // 選択中のフィルタリングを保存されたものから情報を戻してみる
        currentFilter =
            savedInstanceState?.getSerializable(Companion.CURRENT_FILTER) as? TodoRecyclerAdapter.TodoFilter

        // TODOを追加EditTextをイニシャライズ
        initTodoAddEditText()
        // 完了のアイテムを消すボタンをイニシャライズ
        initClearCompletedButton()
        // フィルタリングボタンをイニシャライズ
        initFilteringButton()

        // TODO用のViewModelを準備
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        // LiveDataを見張って、RecyclerViewの情報を更新したり、矢印の色を更新したりします
        todoViewModel.todos.observe(this, Observer { todos ->
            todos?.let {
                // RecyclerViewの情報を更新
                todoRecyclerAdapter.setTodos(todos, currentFilter)
                // データによって、完了アイテムを削除ボタンのビジビリティを更新
                clear_completed_button.visibility =
                    if (todos.none { it.done }) View.GONE else View.VISIBLE
            }
        })
    }

    /**
     * TODOを追加EditTextのイニシャライズ関数です。
     */
    private fun initTodoAddEditText() {
        // テキストフィールドを見張って、もし改行文字であれば、新しいTODOを追加し、フィールドをリセット
        add_todo_editText.afterTextChanged { content ->
            run {
                // 改行文字を確認 (実際は改行があれば、必ず最後に追加された文字のはずです)
                if (content.contains("\n")) {
                    //もしあれば、改行文字削除する
                    val todo = Todo(0, content.replace("\n", ""), false)
                    //新登録処理を呼ぶ
                    todoViewModel.insertTodo(todo)
                    // 内容を消します
                    add_todo_editText.setText("")
                }
            }
        }

        // TODOを追加するテキストフィールドの左にある矢印ボタン押下を見張る
        add_todo_editText.setOnTouchListener { _, event ->
            // 押されたところがあっているかどうかのチェック
            if (event.action == MotionEvent.ACTION_UP &&
                event.rawX <= (add_todo_editText.left + add_todo_editText.compoundDrawables[0].bounds.width())) {
                // もし、全アイテムは完了ではなかったら、全部を完了に更新したり、逆に全部を無完了にしたりする
                if (todoViewModel.todos.value?.none { !it.done }!!) {
                    todoViewModel.updateCompletion(0)
                } else {
                    todoViewModel.updateCompletion(1)
                }
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    /**
     * 完了のTODOを一回で消すボタンをイニシャライズする関数です。
     */
    private fun initClearCompletedButton() {
        // 全完了TODOアイテムを消すOnClickの準備
        clear_completed_button.setOnClickListener {
            todoViewModel.deleteAllCompleted()
        }
    }

    /**
     * フィルタリングのOnClickを設定する関数です。
     */
    private fun initFilteringButton() {
        filter_radio_group.setOnCheckedChangeListener { _, id ->
            currentFilter = when (id) {
                R.id.all_radio_btn -> TodoRecyclerAdapter.TodoFilter.ALL
                R.id.active_radio_btn -> TodoRecyclerAdapter.TodoFilter.ACTIVE
                R.id.completed_radio_btn -> TodoRecyclerAdapter.TodoFilter.COMPLETED
                else -> TodoRecyclerAdapter.TodoFilter.ALL
            }
            todoRecyclerAdapter.filterData(currentFilter)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(TodoRecyclerAdapter.TodoFilter.ALL.toString(), all_radio_btn.isChecked)
        outState.putBoolean(TodoRecyclerAdapter.TodoFilter.ACTIVE.toString(), active_radio_btn.isChecked)
        outState.putBoolean(TodoRecyclerAdapter.TodoFilter.COMPLETED.toString(), completed_radio_btn.isChecked)
        outState.putSerializable(Companion.CURRENT_FILTER, currentFilter)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        all_radio_btn.isChecked = savedInstanceState.getBoolean(TodoRecyclerAdapter.TodoFilter.ALL.toString())
        active_radio_btn.isChecked = savedInstanceState.getBoolean(TodoRecyclerAdapter.TodoFilter.ACTIVE.toString())
        completed_radio_btn.isChecked = savedInstanceState.getBoolean(TodoRecyclerAdapter.TodoFilter.COMPLETED.toString())
        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        private const val CURRENT_FILTER: String = "current_filter"
    }
}

/**
 * 本アプリでは改行キーは入力するという動きになっているので、
 * そもそものEditTextのafterTextChangedを簡単にoverrideするように再定義します。
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged(editable.toString())
        }
    })
}