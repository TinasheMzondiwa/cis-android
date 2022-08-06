package com.tinashe.hymnal.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.constants.UiPref
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter

object Helper {

    fun getJson(resources: Resources, resId: Int): String {
        val resourceReader = resources.openRawResource(resId)
        val writer = StringWriter()

        try {
            val reader = BufferedReader(InputStreamReader(resourceReader, "UTF-8") as Reader)
            var line: String? = reader.readLine()
            while (line != null) {
                writer.write(line)
                line = reader.readLine()
            }
            return writer.toString()
        } catch (e: Exception) {
            Timber.e("Unhandled exception while using JSONResourceReader")
        } finally {
            try {
                resourceReader.close()
            } catch (e: Exception) {
                Timber.e(e, "Unhandled exception while using JSONResourceReader")
            }
        }
        return ""
    }

    /**
     * Return a json string from a File
     */
    @Throws(Exception::class)
    fun getJson(file: File): String {
        val `is`: InputStream = FileInputStream(file)
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            sb.append(line).append("\n")

            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }

    /**
     * Switch to theme based on [UiPref] selected by user
     */
    @JvmStatic
    fun switchToTheme(pref: UiPref) {
        val theme = when (pref) {
            UiPref.DAY -> AppCompatDelegate.MODE_NIGHT_NO
            UiPref.NIGHT -> AppCompatDelegate.MODE_NIGHT_YES
            UiPref.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            UiPref.BATTERY_SAVER -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }

        AppCompatDelegate.setDefaultNightMode(theme)
    }

    fun sendFeedback(activity: Activity) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(activity.getString(R.string.app_email)))
            putExtra(
                Intent.EXTRA_SUBJECT,
                "${activity.getString(R.string.app_full_name)} v${BuildConfig.VERSION_NAME}"
            )
        }
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(
                Intent.createChooser(intent, activity.getString(R.string.send_with))
            )
        }
    }

    fun launchWebUrl(context: Context, url: String) {
        val builder = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .enableUrlBarHiding()
            .setStartAnimations(context, R.anim.slide_up, android.R.anim.fade_out)
            .setExitAnimations(context, android.R.anim.fade_in, R.anim.slide_down)
        try {
            val intent = builder.build()
            intent.launchUrl(context, Uri.parse(url))
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }
}
