package hymnal.hymns

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiState
import hymnal.hymns.model.HymnModel
import kotlinx.collections.immutable.ImmutableList

sealed interface State : CircuitUiState {
    data object Loading : State

    @Immutable
    data class Content(
        val title: String,
        val hymns: ImmutableList<HymnModel>
    ) : State
}