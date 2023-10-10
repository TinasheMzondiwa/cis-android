package hymnal.android.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

interface Scopable {
    val scope: CoroutineScope
}

fun ioScopable(dispatcherProvider: DispatcherProvider): Scopable = object : Scopable {
    override val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)
}

fun mainScopable(dispatcherProvider: DispatcherProvider): Scopable = object : Scopable {
    override val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
}
