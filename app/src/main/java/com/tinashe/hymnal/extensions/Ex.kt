package com.tinashe.hymnal.extensions

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import java.util.*

/**
 * Returns a random element.
 */
fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null

fun String.toColor() = Color.parseColor(this)

fun Drawable.tint(color: Int) {
    DrawableCompat.wrap(this)
    DrawableCompat.setTint(this, color)
    DrawableCompat.unwrap<Drawable>(this)
}
