package com.tinashe.hymnal.ui.widget

import android.text.Editable
import android.widget.EditText
import androidx.core.text.isDigitsOnly

class NumPadWatcher(
    private val editText: EditText,
    private val callbacks: Callbacks
) : SimpleTextWatcher() {

    interface Callbacks {
        fun displayValidNumber(number: String)
        fun onInputEmpty()
    }

    override fun afterTextChanged(editable: Editable?) {
        if (editable.isNullOrEmpty()) {
            callbacks.onInputEmpty()
        } else {
            var input = editable.toString().trim()
            if (input.isDigitsOnly() && input.isNotEmpty()) {
                if (input.startsWith("0")) {
                    input = input.substringAfter("0")
                    if (input.isEmpty()) {
                        clearText()
                        callbacks.onInputEmpty()
                        return
                    }
                }
                callbacks.displayValidNumber(input)
            } else {
                clearText()
            }
        }
    }

    private fun clearText() {
        editText.removeTextChangedListener(this)
        editText.setText("")
        editText.addTextChangedListener(this)
    }
}
