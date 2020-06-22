package com.tinashe.hymnal.ui.hymns.sing

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentSingBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.AppBarBehaviour
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingHymnsFragment : Fragment() {

    private val viewModel: SingHymnsViewModel by viewModels()

    private var pagerAdapter: SingFragmentsAdapter? = null
    private var binding: FragmentSingBinding? = null

    private var appBarBehaviour: AppBarBehaviour? = null

    private val numPadContainer: ConstraintLayout? get() = binding?.sheet?.findViewById(R.id.numPadContainer)
    private val btnBackSpace: View? get() = binding?.sheet?.findViewById(R.id.btnBackSpace)
    private val txtNumber: EditText? get() = binding?.sheet?.findViewById(R.id.txtNumber)
    private val btnGo: Button? get() = binding?.sheet?.findViewById(R.id.btnGo)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appBarBehaviour = context as? AppBarBehaviour
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.hymn_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSingBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val number = arguments?.getInt(getString(R.string.argument_selected_hymn)) ?: 1

        viewModel.statusLiveData.observeNonNull(viewLifecycleOwner) {
        }
        viewModel.hymnalTitleLiveData.observeNonNull(viewLifecycleOwner) {
            appBarBehaviour?.setAppBarTitle(it)
        }
        viewModel.hymnListLiveData.observeNonNull(viewLifecycleOwner) {
            pagerAdapter = SingFragmentsAdapter(this, it)
            binding?.viewPager?.apply {
                adapter = pagerAdapter
                setCurrentItem(number - 1, false)
            }
        }

        binding?.apply {
            numPadContainer?.children?.forEach {
                if (it is Button) {
                    it.setOnClickListener(btnClick)
                }
            }

            txtNumber?.apply {
                val watcher = NumPadWatcher(
                    this,
                    object : NumPadWatcher.Callbacks {
                        override fun displayValidNumber(number: String) {
                            btnGo?.isInvisible = false
                        }

                        override fun onInputEmpty() {
                            btnGo?.isInvisible = true
                        }
                    }
                )
                txtNumber?.addTextChangedListener(watcher)
                txtNumber?.setOnTouchListener { _, _ ->
                    true
                }

                btnBackSpace?.setOnClickListener {
                    val length = txtNumber?.text?.length ?: return@setOnClickListener
                    if (length > 0) {
                        txtNumber?.text?.delete(length - 1, length)
                    }
                }
            }

            fabNumber.setOnClickListener {
                txtNumber?.setText("")
                fabNumber.isExpanded = true
                scrimOverLay.isVisible = true
            }
            scrimOverLay.setOnTouchListener { _, _ ->
                if (fabNumber.isExpanded) {
                    hideNumPad()
                }
                true
            }
        }
    }

    private val btnClick = View.OnClickListener {
        if (it.id == R.id.btnGo) {
            hideNumPad()
            val number = txtNumber?.text?.toString() ?: return@OnClickListener
            if (number.isDigitsOnly()) {
                binding?.viewPager?.setCurrentItem((number.toInt().minus(1)), false)
            }
        } else if (it is Button) {
            txtNumber?.text?.append(it.text)
        }
    }

    private fun hideNumPad() {
        binding?.apply {
            scrimOverLay.isVisible = false
            fabNumber.isExpanded = false
        }
    }

    fun didHandleBackPress(): Boolean {
        return binding?.let {
            if (it.fabNumber.isExpanded) {
                hideNumPad()
                true
            } else {
                false
            }
        } ?: false
    }
}
