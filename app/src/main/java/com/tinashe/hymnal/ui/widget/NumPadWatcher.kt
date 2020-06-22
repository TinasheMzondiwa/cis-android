package com.tinashe.hymnal.ui.widget

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.text.isDigitsOnly

class NumPadWatcher(
    private val editText: EditText,
    private val callbacks: Callbacks
) : TextWatcher {

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

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // no op
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // no op
    }
}
