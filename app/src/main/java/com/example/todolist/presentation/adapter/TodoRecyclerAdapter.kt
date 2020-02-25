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


class TodoRecyclerAdapter(
    private val context: Context,
    val onToggleClick: (Todo, Boolean) -> Unit,
    val onTodoTextChanged: (Todo) -> Unit,
    val onDeleteClick: (Todo) -> Unit
) :
    RecyclerView.Adapter<TodoRecyclerAdapter.TodoViewHolder>() {

    private var todos = emptyList<Todo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(itemView)
    }

    override fun getItemCount() = todos.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bindTodo(todos[position])

        // TODO can focus on done item
        // TODO edit is weird

        holder.itemView.todo_content.afterTextChanged { content ->
            run {
                if (content.contains("\n")) {
                    holder.itemView.todo_content.setText(content.replace("\n", ""))
                    todos[position].mContent = holder.itemView.todo_content.text.toString()
                    onTodoTextChanged(todos[position])
                    holder.itemView.todo_content.clearFocus()
                }
            }
        }

        holder.itemView.todo_content.setOnFocusChangeListener { _, focused ->
            holder.itemView.delete_image.visibility = if (focused) View.VISIBLE else View.GONE
        }

        holder.itemView.delete_image.setOnClickListener {
            onDeleteClick(todos[position])
        }

        //
        holder.itemView.done_checkbox.setOnClickListener {
            onToggleClick(todos[position], (it as CompoundButton).isChecked)
        }

    }

    fun setTodos(todos: List<Todo>) {
        this.todos = todos
        notifyDataSetChanged()
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val strikethroughSpan = StrikethroughSpan()

        fun bindTodo(todo: Todo) {
            with(todo) {
                itemView.todo_content.setText(mContent)
                itemView.done_checkbox.isChecked = mDone
                if (mDone) {
                    itemView.todo_content.setTextColor(
                        ContextCompat.getColor(context, R.color.colorDisableText)
                    )
                    itemView.todo_content.text.setSpan(
                        strikethroughSpan,
                        0,
                        itemView.todo_content.text.length - 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    itemView.todo_content.setTextColor(
                        ContextCompat.getColor(context, R.color.colorEnableText)
                    )
                    itemView.todo_content.text.removeSpan(strikethroughSpan)
                }
            }
        }
    }

}
