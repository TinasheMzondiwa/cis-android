package services.hymnal.prefs.impl

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import hymnal.android.coroutines.DispatcherProvider
import hymnal.android.coroutines.Scopable
import hymnal.android.coroutines.ioScopable
import hymnal.prefs.HymnalPrefs
import hymnal.prefs.model.HymnalSort
import hymnal.prefs.model.TextStyleModel
import hymnal.prefs.model.UiPref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import library.hymnal.fonts.R as FontsR

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "hymnal_prefs",
    produceMigrations = {
        listOf(
            SharedPreferencesMigration(
                it,
                "${it.packageName}_preferences",
                setOf(HymnalPrefsImpl.KEY_CODE)
            )
        )
    }
)

class HymnalPrefsImpl @Inject constructor(
    @ApplicationContext context: Context,
    dispatcherProvider: DispatcherProvider
) : HymnalPrefs, Scopable by ioScopable(dispatcherProvider) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val dataStore: DataStore<Preferences> = context.dataStore

    private fun preferencesFlow(): Flow<Preferences> = dataStore.data
        .catch { exception ->
            Timber.e(exception)
            emit(emptyPreferences())
        }

    override fun getSelectedHymnal(): String = prefs.getString(KEY_CODE, DEFAULT_CODE)!!

    override fun getSelected(): Flow<String> = preferencesFlow()
        .map { it[stringPreferencesKey(KEY_CODE)] ?: DEFAULT_CODE }

    override fun setSelectedHymnal(code: String) {
        prefs.edit { putString(KEY_CODE, code) }
        scope.launch { dataStore.edit { it[stringPreferencesKey(KEY_CODE)] = code } }
    }

    override fun getUiPref(): UiPref {
        val value = prefs.getString(KEY_UI_PREF, null) ?: return UiPref.FOLLOW_SYSTEM
        return UiPref.fromString(value) ?: UiPref.FOLLOW_SYSTEM
    }

    override fun setUiPref(pref: UiPref) {
        prefs.edit {
            putString(KEY_UI_PREF, pref.value)
        }
    }

    override fun setFontRes(fontRes: Int) {
        prefs.edit { putInt(KEY_FONT_STYLE, fontRes) }
    }

    override fun getFontRes(): Int = prefs.getInt(KEY_FONT_STYLE, FontsR.font.proxima_nova_soft_regular)

    override fun setFontSize(size: Float) {
        prefs.edit { putFloat(KEY_FONT_SIZE, size) }
    }

    override fun getFontSize(): Float = prefs.getFloat(KEY_FONT_SIZE, 22f)

    override fun getTextStyleModel(): TextStyleModel = TextStyleModel(
        getUiPref(),
        getFontRes(),
        getFontSize()
    )

    override fun isHymnalPromptSeen(): Boolean = prefs.getBoolean(KEY_HYMNALS_PROMPT, false)

    override fun setHymnalPromptSeen() {
        prefs.edit {
            putBoolean(KEY_HYMNALS_PROMPT, true)
        }
    }

    override fun getHymnalSort(): HymnalSort {
        val name = prefs.getString(KEY_HYMNAL_SORT, null)
        return name?.let {
            HymnalSort.fromString(it)
        } ?: HymnalSort.TITLE
    }

    override fun setHymnalSort(sort: HymnalSort) {
        prefs.edit {
            putString(KEY_HYMNAL_SORT, sort.name)
        }
    }

    companion object {
        private const val DEFAULT_CODE = "english"

        internal const val KEY_CODE = "pref:default_code"
        private const val KEY_UI_PREF = "pref:app_theme"
        private const val KEY_FONT_STYLE = "pref:font_res"
        private const val KEY_FONT_SIZE = "pref:font_size"
        private const val KEY_HYMNALS_PROMPT = "pref:hymnals_list_prompt"
        private const val KEY_HYMNAL_SORT = "pref:hymnal_sort_options"
    }
}