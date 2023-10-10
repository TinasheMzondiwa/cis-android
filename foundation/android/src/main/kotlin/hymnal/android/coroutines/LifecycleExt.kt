package hymnal.android.coroutines

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A convenience wrapper around [flowWithLifecycle] that simply calls [collect]
 * with [action]. Think of it as [kotlinx.coroutines.flow.launchIn], but for collecting.
 *
 * ```
 * uiStateFlow.collectIn(owner) { uiState ->
 *   updateUi(uiState)
 * }
 * ```
 */
inline fun <T> Flow<T>.collectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    flowWithLifecycle(owner.lifecycle, minActiveState).collect { action(it) }
}