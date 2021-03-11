package com.tinashe.hymnal.extensions.coroutines

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class SchedulerProvider(
    val io: CoroutineContext = Dispatchers.IO,
    val main: CoroutineContext = Dispatchers.Main
)
