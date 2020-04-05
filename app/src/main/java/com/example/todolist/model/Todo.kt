package com.example.todolist.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_item_table")
data class Todo(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "done") var done: Boolean
)
