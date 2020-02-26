package com.example.todolist.presentation.adapter

import android.content.Context
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.model.Todo
import com.example.todolist.presentation.afterTextChanged
import kotlinx.android.synthetic.main.todo_item.view.*
import kotlin.math.max


class TodoRecyclerAdapter(
    private val context: Context,
    val onToggleClick: (Todo, Boolean) -> Unit,
    val onTodoTextChanged: (Todo) -> Unit,
    val onDeleteClick: (Todo) -> Unit
) : RecyclerView.Adapter<TodoRecyclerAdapter.TodoViewHolder>() {

    // 表示するデータ
    private var todos = emptyList<Todo>()

    /**
     * アダプターがどのレイアウトを追加うかを設定する関数
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(itemView)
    }

    /**
     * 表示するアイテムの数
     */
    override fun getItemCount() = todos.size

    /**
     * マイアイテムにどの情報がどこ表示するかを設定するのはここで設定できます。
     */
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        // まずは度のデータがどのレイアウトにアサインするかを設定する
        holder.bindTodo(todos[holder.adapterPosition])

        // もし、アイテムは完了であれば、
        if(!todos[holder.adapterPosition].mDone) {
            holder.itemView.todo_content.afterTextChanged { content ->
                run {
                    // 改行文字があれば、消して、TODOを更新する
                    if (content.contains("\n")) {
                        todos[holder.adapterPosition].mContent = content.replace("\n", "")
                        onTodoTextChanged(todos[holder.adapterPosition])
                        //フォカストをリセットする
                        holder.itemView.todo_content.clearFocus()
                    }
                }
            }
        }

        // アイテムのフォカストによって、削除アイコンを表示する
        holder.itemView.todo_content.setOnFocusChangeListener { _, hasFocus ->
            holder.itemView.delete_image.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }

        // 削除アイコンを押したら、TODO削除処理を呼ぶ
        holder.itemView.delete_image.setOnClickListener {
            onDeleteClick(todos[holder.adapterPosition])
        }

        // チェックボックスを押すと、TODOアイテムの完了か無完了状態を更新する
        holder.itemView.done_checkbox.setOnClickListener {
            onToggleClick(todos[holder.adapterPosition], (it as CompoundButton).isChecked)
        }

    }

    /**
     * 表示するデータを更新する関数です
     */
    fun setTodos(todos: List<Todo>) {
        this.todos = todos
        notifyDataSetChanged()
    }

    /**
     * アダプター用のホルダークラスです。
     * ここではbindTodo関数を使ってアダプターの情報をビューにバインドできます
     */
    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val strikethroughSpan = StrikethroughSpan()

        /**
         * アダプターの情報（todoアイテム）をビューにバインドできます
         */
        fun bindTodo(todo: Todo) {
            with(todo) {
                itemView.todo_content.setText(mContent)
                itemView.done_checkbox.isChecked = mDone
                // もし完了のアイテムであれば、テキストの色や形を変更します
                if (mDone) {
                    //色はグレーにする
                    itemView.todo_content.setTextColor(
                        ContextCompat.getColor(context, R.color.colorDisableText)
                    )
                    // 文章を取り消し線を追加（テキストの中央部分に重ねて引いた線のことです）
                    itemView.todo_content.text.setSpan(
                        strikethroughSpan,
                        0,
                        max(itemView.todo_content.text.lastIndex, 0),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                } else {
                    // 無完了なら、色は黒で、取り消し線を消す
                    itemView.todo_content.setTextColor(
                        ContextCompat.getColor(context, R.color.colorEnableText)
                    )
                    itemView.todo_content.text.removeSpan(strikethroughSpan)
                }
            }
        }
    }

}
