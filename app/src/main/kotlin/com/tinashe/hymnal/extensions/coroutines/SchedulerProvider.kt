package com.tinashe.hymnal.extensions.coroutines

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SchedulerProvider @Inject constructor() {

    val io: CoroutineContext = Dispatchers.IO
    val main: CoroutineContext = Dispatchers.Main
}
