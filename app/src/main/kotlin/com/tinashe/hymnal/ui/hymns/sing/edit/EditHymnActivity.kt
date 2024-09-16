package com.tinashe.hymnal.ui.hymns.sing.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.ActivityEditHymnBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.extensions.view.viewBinding
import com.tinashe.hymnal.ui.widget.SimpleTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import hymnal.content.model.Hymn
import hymnal.content.model.Status
import org.wordpress.aztec.Aztec
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.toolbar.IAztecToolbarClickListener
import hymnal.l10n.R as L10nR

@AndroidEntryPoint
class EditHymnActivity : AppCompatActivity(), IAztecToolbarClickListener {

    private val viewModel: EditHymnViewModel by viewModels()
    private val binding by viewBinding(ActivityEditHymnBinding::inflate)

    private val contentWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(editable: Editable?) {
            super.afterTextChanged(editable)
            binding.btnSave.isVisible = editable?.isNotEmpty() == true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initUi()

        viewModel.statusLiveData.observeNonNull(this) { status ->
            when (status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    setResult(Activity.RESULT_OK)
                    finishAfterTransition()
                }
                Status.ERROR -> {
                    binding.snackbar.show(
                        messageId = L10nR.string.error_invalid_content,
                        longDuration = true
                    )
                }
            }
        }
        viewModel.editContentLiveData.observeNonNull(this) { content ->
            binding.edtHymn.apply {
                removeTextChangedListener(contentWatcher)
                fromHtml(content.first)
                addTextChangedListener(contentWatcher)
            }
            invalidateOptionsMenu()
        }
    }

    private fun initUi() {
        with(binding) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            btnSave.setOnClickListener {
                MaterialAlertDialogBuilder(this@EditHymnActivity)
                    .setMessage(L10nR.string.confirm_save)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(L10nR.string.title_save) { _, _ ->
                        val html = edtHymn.toPlainHtml()
                        viewModel.saveContent(html)
                    }
                    .show()
            }

            Aztec.with(edtHymn, edtToolbar, this@EditHymnActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_hymn, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val visible = viewModel.editContentLiveData.value?.second ?: false
        menu?.findItem(R.id.action_undo)?.isVisible = visible
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                true
            }
            R.id.action_undo -> {
                MaterialAlertDialogBuilder(this@EditHymnActivity)
                    .setMessage(L10nR.string.undo_changes_confirm)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(L10nR.string.title_undo) { _, _ ->
                        viewModel.undoChanges()
                    }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onToolbarCollapseButtonClicked() {
    }

    override fun onToolbarExpandButtonClicked() {
    }

    override fun onToolbarFormatButtonClicked(format: ITextFormat, isKeyboardShortcut: Boolean) {
    }

    override fun onToolbarHeadingButtonClicked() {
    }

    override fun onToolbarHtmlButtonClicked() {
    }

    override fun onToolbarListButtonClicked() {
    }

    override fun onToolbarMediaButtonClicked(): Boolean = false

    companion object {
        fun editIntent(context: Context, hymn: Hymn): Intent = Intent(
            context,
            EditHymnActivity::class.java
        ).apply {
            putExtra(EXTRA_HYMN, hymn)
        }
    }
}
