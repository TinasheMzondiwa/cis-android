package com.tinashe.hymnal.data.model.constants

import androidx.annotation.RawRes
import com.tinashe.hymnal.R

enum class Hymnals(
    val key: String,
    val title: String,
    val language: String,
    @RawRes val resId: Int
) {

    ENGLISH("english", "Christ In Song", "English", R.raw.english),
    TSWANA("tswana", "Keresete Mo Kopelong", "Tswana", R.raw.tswana),
    SOTHO("sotho", "Keresete Pineng", "Sotho", R.raw.sotho),
    CHICHEWA("chichewa", "Khristu Mu Nyimbo", "Chichewa", R.raw.chichewa),
    TONGA("tonga", "Kristu Mu Nyimbo", "Tonga", R.raw.tonga),
    SHONA("shona", "Kristu MuNzwiyo", "Shona", R.raw.shona),
    VENDA("venda", "Ngosha YaDzingosha", "Venda", R.raw.venda),
    SWAHILI("swahili", "Nyimbo Za Kristo", "Swahili", R.raw.swahili),
    NDEBELE("ndebele", "UKrestu Esihlabelelweni", "Ndebele/IsiZulu", R.raw.ndebele),
    XHOSA("xhosa", "UKristu Engomeni", "IsiXhosa", R.raw.xhosa),
    XITSONGA("xitsonga", "Risima Ra Vuyimbeleri", "Xitsonga", R.raw.xitsonga);

    companion object {
        private val map = values().associateBy(Hymnals::key)

        fun fromString(key: String) = map[key]
    }
}
