package com.tinashe.hymnal.extensions.activity

import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

/**
 * get a material container arc transform
 */
fun getContentTransform(): MaterialContainerTransform {
    return MaterialContainerTransform().apply {
        addTarget(android.R.id.content)
        duration = 450
        pathMotion = MaterialArcMotion()
    }
}

/**
 * apply material exit container transformation
 */
fun AppCompatActivity.applyExitMaterialTransform() {
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    window.sharedElementsUseOverlay = false
}

/**
 * apply material entered container transformation
 */
fun AppCompatActivity.applyMaterialTransform(transitionName: String) {
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    ViewCompat.setTransitionName(findViewById(android.R.id.content), transitionName)

    setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    window.sharedElementEnterTransition = getContentTransform()
    window.sharedElementReturnTransition = getContentTransform()
}
