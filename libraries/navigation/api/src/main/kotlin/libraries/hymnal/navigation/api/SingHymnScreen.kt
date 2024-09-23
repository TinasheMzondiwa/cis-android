package libraries.hymnal.navigation.api

import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingHymnScreen(val hymnId: Int, val collectionId: Int? = null) : Screen
