package com.example.todolist.presentation.util

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText


/**
 * EditTextをベースにして、カストマイズなEditTextクラスです。
 * なぜか、特別な動きをしたいです。例えば、バックキーを押すと、フォーカスをリセットしたかったです。
 */
class ClearFocusEditText: EditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus()
        }
        return super.onKeyPreIme(keyCode, event)
    }
}