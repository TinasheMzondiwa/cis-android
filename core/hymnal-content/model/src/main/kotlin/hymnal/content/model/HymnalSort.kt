package hymnal.content.model

enum class HymnalSort {
    TITLE,
    LANGUAGE;

    companion object {
        private val map = values().associateBy(HymnalSort::name)

        fun fromString(name: String) = map[name]
    }
}
