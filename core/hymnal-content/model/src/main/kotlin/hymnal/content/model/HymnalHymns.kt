package hymnal.content.model

data class HymnalHymns(
    val code: String,
    val title: String,
    val language: String,
    val hymns: List<Hymn>
) {
    constructor(hymnal: Hymnal, hymns: List<Hymn>) : this(
        hymnal.code,
        hymnal.title,
        hymnal.language,
        hymns
    )
}
