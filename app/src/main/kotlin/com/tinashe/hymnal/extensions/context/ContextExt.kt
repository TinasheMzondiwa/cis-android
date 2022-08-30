package com.tinashe.hymnal.extensions.context

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.getColorOrThrow
import com.tinashe.hymnal.R

@ColorInt
fun Context.getColorPrimary() = getAttrColor(R.attr.colorPrimary)

@ColorInt
fun Context.getAttrColor(@AttrRes attrColor: Int) = theme.obtainStyledAttributes(intArrayOf(attrColor)).getColorOrThrow(0)
