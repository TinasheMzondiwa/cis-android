package hymnal.content.model

enum class Hymnals(
    val key: String,
    val title: String,
    val language: String,
) {

    ENGLISH("english", "Christ In Song", "English"),
    TSWANA("tswana", "Keresete Mo Kopelong", "Tswana"),
    SOTHO("sotho", "Keresete Pineng", "Sotho"),
    CHICHEWA("chichewa", "Khristu Mu Nyimbo", "Chichewa"),
    TONGA("tonga", "Kristu Mu Nyimbo", "Tonga"),
    SHONA("shona", "Kristu MuNzwiyo", "Shona"),
    VENDA("venda", "Ngosha YaDzingosha", "Venda"),
    SWAHILI("swahili", "Nyimbo Za Kristo", "Swahili"),
    NDEBELE("ndebele", "UKrestu Esihlabelelweni", "Ndebele/IsiZulu"),
    XHOSA("xhosa", "UKristu Engomeni", "IsiXhosa"),
    XITSONGA("xitsonga", "Risima Ra Vuyimbeleri", "Xitsonga"),
    GIKUYU("gikuyu", "Nyimbo cia Agendi", "Kikuyu"),
    ABAGUSII("abagusii", "Ogotera kw'ogotogia Nyasae", "Abagusii"),
    DHOLUO("dholuo", "Wende Nyasaye", "Dholuo"),
    SDAH("sdah", "SDA Hymnal", "English");

    companion object {
        private val map = values().associateBy(Hymnals::key)

        fun fromString(key: String) = map[key]
    }
}
