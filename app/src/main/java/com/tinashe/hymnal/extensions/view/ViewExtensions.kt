package com.tinashe.hymnal.extensions.view

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

fun inflateView(@LayoutRes layoutResId: Int, parent: ViewGroup, attachToRoot: Boolean): View =
    LayoutInflater.from(parent.context).inflate(layoutResId, parent, attachToRoot)

fun RecyclerView.smoothScrollToPositionWithSpeed(
    position: Int,
    millisPerInch: Float = 25f
) {
    val scroller = object : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return millisPerInch / displayMetrics.densityDpi
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

    scroller.targetPosition = position
    layoutManager?.startSmoothScroll(scroller)
}
