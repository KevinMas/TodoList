package com.example.todolist.presentation

import android.R.color
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
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
import com.example.todolist.presentation.viewmodel.TodoViewModel
import kotlinx.android.synthetic.main.todo_activity_main.*

/**
 * TODOアプリのアクティビティクラスです。
 */
class TodoActivity : AppCompatActivity() {

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


        // テキストフィールドを見張って、もし改行文字であれば、新しいTODOを追加し、フィールドをリセット
        add_todo_editText.afterTextChanged { content ->
            run {
                // 改行文字を確認
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
            if (event.action == MotionEvent.ACTION_UP) {
                // 押されたところがあっているかどうかのチェック
                if (event.rawX <= (add_todo_editText.left +
                            add_todo_editText.compoundDrawables[0].bounds.width())) {
                    // もし、全アイテムは完了ではなかったら、全部を完了に更新したり、逆に全部を無完了にしたりする
                    if (mTodoViewModel.todos.value?.none { !it.mDone }!!) {
                        mTodoViewModel.updateCompletion(0)
                    } else {
                        mTodoViewModel.updateCompletion(1)
                    }
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        // TODO用のViewModelを準備
        mTodoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        // LiveDataを見張って、RecyclerViewの情報を更新したり、矢印の色を更新したりします
        mTodoViewModel.todos.observe(this, Observer { todos ->
            todos?.let {
                // RecyclerViewの情報を更新
                mAdapter.setTodos(todos)
                // アイテムによると、矢印の色を更新（黒は全TODOが完了、グレーはTODOが残る）
                if (todos.none { !it.mDone }) {
                    changeSVGColor(
                        resources.getDrawable(R.drawable.ic_keyboard_arrow_down, theme),
                        R.color.colorEnableText
                    )
                } else {
                    changeSVGColor(
                        resources.getDrawable(R.drawable.ic_keyboard_arrow_down, theme),
                        R.color.colorDisableText
                    )
                }
                // データによって、完了アイテムを削除ボタンのビジビリティを更新
                delete_completed.visibility = if (todos.none{it.mDone}) View.GONE else View.VISIBLE
            }
        })

        // 全完了TODOアイテムを消すOnClickの準備
        delete_completed.setOnClickListener {
            mTodoViewModel.deleteAllCompleted()
        }

    }



    /**
     * 画像の色を更新処理です。OSのバージョンによると、違います。
     */
    private fun changeSVGColor(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter =
                BlendModeColorFilter(resources.getColor(color, theme), BlendMode.SRC_ATOP)
        } else {
            drawable.setColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_ATOP)
        }
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