package com.tinashe.hymnal.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.core.view.children
import androidx.core.view.isInvisible
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.NumberPadBinding
import com.tinashe.hymnal.extensions.view.inflateView

class NumberPadView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val binding: NumberPadBinding by lazy { NumberPadBinding.bind(this) }
    private var numSelected: ((Int) -> Unit)? = null

    init {
        inflateView(R.layout.number_pad, this, true)
    }

    private val btnClick = OnClickListener {
        if (it.id == R.id.btnGo) {
            val number = binding.txtNumber.text?.toString() ?: return@OnClickListener
            if (number.isDigitsOnly()) {
                numSelected?.invoke(number.toInt())
            }
        } else if (it is Button) {
            binding.txtNumber.text?.append(it.text)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        binding.apply {
            val container = numPadContainer.getChildAt(0) as? ViewGroup
            container?.children?.forEach {
                if (it is Button) {
                    it.setOnClickListener(btnClick)
                }
            }

            val watcher = NumPadWatcher(
                txtNumber,
                object : NumPadWatcher.Callbacks {
                    override fun displayValidNumber(number: String) {
                        btnGo.isInvisible = false
                    }

                    override fun onInputEmpty() {
                        btnGo.isInvisible = true
                    }
                }
            )
            txtNumber.addTextChangedListener(watcher)
            txtNumber.setOnTouchListener { _, _ ->
                true
            }

            btnBackSpace.setOnClickListener {
                val length = txtNumber.text?.length ?: return@setOnClickListener
                if (length > 0) {
                    txtNumber.text?.delete(length - 1, length)
                }
            }
        }
    }

    fun setOnNumSelectedCallback(callback: (Int) -> Unit) {
        this.numSelected = callback
    }

    fun onShown() {
        binding.txtNumber.setText("")
    }
}
