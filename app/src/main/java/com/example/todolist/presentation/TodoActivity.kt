package com.example.todolist.presentation

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
import com.example.todolist.data.model.Todo
import com.example.todolist.presentation.adapter.TodoRecyclerAdapter
import com.example.todolist.domain.viewmodel.TodoViewModel
import kotlinx.android.synthetic.main.todo_activity_main.*

/**
 * TODOアプリのアクティビティクラスです。
 */
class TodoActivity : AppCompatActivity() {

    private val CURRENT_FILTER: String = "current_filter"
    private var mCurrentFilter : TodoRecyclerAdapter.TodoFilter? = TodoRecyclerAdapter.TodoFilter.ALL

    private lateinit var mAdapter: TodoRecyclerAdapter
    private lateinit var mTodoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_main)

        // RecyclerViewを準備する
        todo_recyclerView.layoutManager = LinearLayoutManager(this)
        // アダプター作成
        mAdapter = TodoRecyclerAdapter(this,
            { todo, done -> mTodoViewModel.toggleDone(todo, done) },
            { todo -> mTodoViewModel.updateTodo(todo) },
            { todo -> mTodoViewModel.deleteTodo(todo) }
        )
        // RecyclerViewにアダプターを設定
        todo_recyclerView.adapter = mAdapter

        // 選択中のフィルタリングを保存されたものから情報を戻してみる
        mCurrentFilter =
            savedInstanceState?.getSerializable(CURRENT_FILTER) as? TodoRecyclerAdapter.TodoFilter

        // TODOを追加EditTextをイニシャライズ
        initTodoAddEditText()
        // 完了のアイテムを消すボタンをイニシャライズ
        initClearCompletedButton()
        // フィルタリングボタンをイニシャライズ
        initFilteringButton()

        // TODO用のViewModelを準備
        mTodoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        // LiveDataを見張って、RecyclerViewの情報を更新したり、矢印の色を更新したりします
        mTodoViewModel.todos.observe(this, Observer { todos ->
            todos?.let {
                // RecyclerViewの情報を更新
                mAdapter.setTodos(todos, mCurrentFilter)
                // データによって、完了アイテムを削除ボタンのビジビリティを更新
                clear_completed_button.visibility =
                    if (todos.none { it.mDone }) View.GONE else View.VISIBLE
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
                    mTodoViewModel.insertTodo(todo)
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
                if (mTodoViewModel.todos.value?.none { !it.mDone }!!) {
                    mTodoViewModel.updateCompletion(0)
                } else {
                    mTodoViewModel.updateCompletion(1)
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
            mTodoViewModel.deleteAllCompleted()
        }
    }

    /**
     * フィルタリングのOnClickを設定する関数です。
     */
    private fun initFilteringButton() {
        // もし現在フィルタリングフラグはnullであれば、ALLに設定し、ボタンをアクティブし設定する
        if(mCurrentFilter == null ) {
            mCurrentFilter = TodoRecyclerAdapter.TodoFilter.ALL
            all_button.isActivated = true
        }

        all_button.setOnClickListener {
            mCurrentFilter = TodoRecyclerAdapter.TodoFilter.ALL
            all_button.isActivated = true
            active_button.isActivated = false
            completed_button.isActivated = false
            mAdapter.filterData(mCurrentFilter)
    }

        active_button.setOnClickListener {
            mCurrentFilter = TodoRecyclerAdapter.TodoFilter.ACTIVE
            active_button.isActivated = true
            completed_button.isActivated = false
            all_button.isActivated = false
            mAdapter.filterData(mCurrentFilter)
        }

        completed_button.setOnClickListener {
            mCurrentFilter = TodoRecyclerAdapter.TodoFilter.COMPLETED
            completed_button.isActivated = true
            active_button.isActivated = false
            all_button.isActivated = false
            mAdapter.filterData(mCurrentFilter)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(TodoRecyclerAdapter.TodoFilter.ALL.toString(), all_button.isActivated)
        outState.putBoolean(TodoRecyclerAdapter.TodoFilter.ACTIVE.toString(), active_button.isActivated)
        outState.putBoolean(TodoRecyclerAdapter.TodoFilter.COMPLETED.toString(), completed_button.isActivated)
        outState.putSerializable(CURRENT_FILTER, mCurrentFilter)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        all_button.isActivated = savedInstanceState.getBoolean(TodoRecyclerAdapter.TodoFilter.ALL.toString())
        active_button.isActivated = savedInstanceState.getBoolean(TodoRecyclerAdapter.TodoFilter.ACTIVE.toString())
        completed_button.isActivated = savedInstanceState.getBoolean(TodoRecyclerAdapter.TodoFilter.COMPLETED.toString())
        super.onRestoreInstanceState(savedInstanceState)
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