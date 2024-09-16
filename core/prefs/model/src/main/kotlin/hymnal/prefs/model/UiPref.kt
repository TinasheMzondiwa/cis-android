package hymnal.prefs.model

enum class UiPref(val value: String) {
    DAY("day"),
    NIGHT("night"),
    BATTERY_SAVER("battery_saver"),
    FOLLOW_SYSTEM("follow_system");

    companion object {
        private val map = values().associateBy(UiPref::value)

        fun fromString(type: String) = map[type]
    }
}
