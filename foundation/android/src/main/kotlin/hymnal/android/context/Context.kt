package hymnal.android.context

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.getColorOrThrow
import androidx.core.net.toUri
import hymnal.android.R
import timber.log.Timber
import hymnal.l10n.R as L10nR

@ColorInt
fun Context.getColorPrimary() = getAttrColor(androidx.appcompat.R.attr.colorPrimary)

@ColorInt
fun Context.getAttrColor(@AttrRes attrColor: Int) = theme.obtainStyledAttributes(intArrayOf(attrColor)).getColorOrThrow(0)


fun Context.launchWebUrl(url: String): Boolean = launchWebUrl(url.toUri())

fun Context.launchWebUrl(uri: Uri): Boolean {
    // Try using Chrome Custom Tabs.
    try {
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .setStartAnimations(this, R.anim.slide_up, android.R.anim.fade_out)
            .setExitAnimations(this, android.R.anim.fade_in, R.anim.slide_down)
            .build()
        intent.launchUrl(this, uri)
        return true
    } catch (ignored: Exception) {
        Timber.e(ignored)
    }

    // Fall back to launching a default web browser intent.
    try {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            return true
        }
    } catch (ignored: Exception) {
        Timber.e(ignored)
    }

    // We were unable to show the web page.
    return false
}

fun Context.sendFeedback(versionName: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(L10nR.string.app_email)))
        putExtra(
            Intent.EXTRA_SUBJECT,
            "${getString(L10nR.string.app_full_name)} v$versionName"
        )
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(
            Intent.createChooser(intent, getString(L10nR.string.send_with))
        )
    }
}