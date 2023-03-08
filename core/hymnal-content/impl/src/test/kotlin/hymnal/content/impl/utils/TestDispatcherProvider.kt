package hymnal.content.impl.utils

import hymnal.android.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Inject

class TestDispatcherProvider(private val testDispatcher: TestDispatcher) : DispatcherProvider {

    @Inject
    constructor() : this(testDispatcher = UnconfinedTestDispatcher())

    override val io: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}
