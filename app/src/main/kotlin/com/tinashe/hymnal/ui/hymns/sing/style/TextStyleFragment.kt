package com.tinashe.hymnal.ui.hymns.sing.style

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentTextOptionsBinding
import hymnal.android.intent.getParcelableCompat
import hymnal.prefs.model.TextStyleModel
import hymnal.prefs.model.UiPref
import library.hymnal.fonts.R as FontsR

class TextStyleFragment : BottomSheetDialogFragment() {

    private var styleChanges: TextStyleChanges? = null

    private lateinit var binding: FragmentTextOptionsBinding

    override fun getTheme(): Int = R.style.ThemeOverlay_CIS_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_text_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTextOptionsBinding.bind(view)

        val model = arguments?.getParcelableCompat(ARG_MODEL, TextStyleModel::class.java)
            ?: return

        binding.apply {
            chipGroupTheme.apply {
                check(
                    when (model.pref) {
                        UiPref.DAY -> R.id.chipThemeLight
                        UiPref.NIGHT -> R.id.chipThemeDark
                        else -> R.id.chipThemeSystem
                    }
                )
                setOnCheckedStateChangeListener { _, checkedIds ->
                    checkedIds.forEach { checkedId ->
                        when (checkedId) {
                            R.id.chipThemeLight -> UiPref.DAY
                            R.id.chipThemeDark -> UiPref.NIGHT
                            R.id.chipThemeSystem -> UiPref.FOLLOW_SYSTEM
                            else -> null
                        }?.let {
                            styleChanges?.updateTheme(it)
                            dismiss()
                        }
                    }
                }
            }

            chipGroupTypeface.apply {
                check(
                    when (model.fontRes) {
                        FontsR.font.lato -> R.id.chipTypefaceLato
                        FontsR.font.andada -> R.id.chipTypefaceAndada
                        FontsR.font.roboto -> R.id.chipTypefaceRoboto
                        FontsR.font.gentium_basic -> R.id.chipTypefaceGentium
                        else -> R.id.chipTypefaceProxima
                    }
                )

                setOnCheckedStateChangeListener { _, checkedIds ->
                    checkedIds.forEach { checkedId ->
                        when (checkedId) {
                            R.id.chipTypefaceLato -> FontsR.font.lato
                            R.id.chipTypefaceAndada -> FontsR.font.andada
                            R.id.chipTypefaceRoboto -> FontsR.font.roboto
                            R.id.chipTypefaceGentium -> FontsR.font.gentium_basic
                            R.id.chipTypefaceProxima -> FontsR.font.proxima_nova_soft_regular
                            else -> null
                        }?.let { typeface ->
                            styleChanges?.updateTypeFace(typeface)
                        }
                    }
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
