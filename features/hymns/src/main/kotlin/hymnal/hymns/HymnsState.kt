package hymnal.hymns

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.hymns.model.HymnModel
import kotlinx.collections.immutable.ImmutableList
import hymnal.l10n.R as L10nR

sealed interface State : CircuitUiState {
    data object Loading : State

    @Immutable
    data class Content(
        val sortType: SortType,
        val title: String,
        val hymns: ImmutableList<HymnModel>,
        val eventSink: (Event) -> Unit,
    ) : State
}

sealed interface Event : CircuitUiEvent {
    data object OnSortClicked : Event
    data class OnHymnClicked(val hymn: HymnModel) : Event

}

enum class SortType(@StringRes val title: Int, val icon: ImageVector) {
    NUMBER(L10nR.string.sort_number, Icons.Rounded.Numbers),
    TITLE(L10nR.string.sort_title, Icons.Rounded.SortByAlpha),
}

fun SortType.next(): SortType = when (this) {
    SortType.NUMBER -> SortType.TITLE
    SortType.TITLE -> SortType.NUMBER
}