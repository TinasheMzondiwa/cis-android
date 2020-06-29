package com.tinashe.hymnal.ui.hymns.sing.style

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.TextStyleModel
import com.tinashe.hymnal.data.model.constants.UiPref
import com.tinashe.hymnal.databinding.FragmentTextOptionsBinding

class TextStyleFragment : BottomSheetDialogFragment() {

    private var styleChanges: TextStyleChanges? = null

    private var binding: FragmentTextOptionsBinding? = null

    override fun getTheme(): Int = R.style.ThemeOverlay_CIS_BottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentTextOptionsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = arguments?.get(ARG_MODEL) as? TextStyleModel
            ?: return

        binding?.apply {
            chipGroupTheme.apply {
                check(
                    when (model.pref) {
                        UiPref.DAY -> R.id.chipThemeLight
                        UiPref.NIGHT -> R.id.chipThemeDark
                        else -> R.id.chipThemeSystem
                    }
                )
                setOnCheckedChangeListener { _, checkedId ->
                    val pref = when (checkedId) {
                        R.id.chipThemeLight -> UiPref.DAY
                        R.id.chipThemeDark -> UiPref.NIGHT
                        R.id.chipThemeSystem -> UiPref.FOLLOW_SYSTEM
                        else -> return@setOnCheckedChangeListener
                    }
                    styleChanges?.updateTheme(pref)
                    dismiss()
                }
            }

            chipGroupTypeface.apply {
                check(
                    when (model.fontRes) {
                        R.font.lato -> R.id.chipTypefaceLato
                        R.font.andada -> R.id.chipTypefaceAndada
                        R.font.roboto -> R.id.chipTypefaceRoboto
                        R.font.gentium_basic -> R.id.chipTypefaceGentium
                        else -> R.id.chipTypefaceProxima
                    }
                )

                setOnCheckedChangeListener { _, checkedId ->
                    val typeface = when (checkedId) {
                        R.id.chipTypefaceLato -> R.font.lato
                        R.id.chipTypefaceAndada -> R.font.andada
                        R.id.chipTypefaceRoboto -> R.font.roboto
                        R.id.chipTypefaceGentium -> R.font.gentium_basic
                        R.id.chipTypefaceProxima -> R.font.proxima_nova_soft_regular
                        else -> return@setOnCheckedChangeListener
                    }
                    styleChanges?.updateTypeFace(typeface)
                }

                sizeSlider.apply {
                    value = model.textSize
                    addOnChangeListener { _, value, fromUser ->
                        if (fromUser) {
                            styleChanges?.updateTextSize(value)
                        }
                    }
                    setLabelFormatter {
                        when (it) {
                            14f -> "xSmall"
                            18f -> "Small"
                            22f -> "Medium"
                            26f -> "Large"
                            30f -> "xLarge"
                            else -> ""
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_MODEL = "arg:model"

        fun newInstance(model: TextStyleModel, callback: TextStyleChanges): TextStyleFragment =
            TextStyleFragment().apply {
                styleChanges = callback
                arguments = bundleOf(ARG_MODEL to model)
            }
    }
}
