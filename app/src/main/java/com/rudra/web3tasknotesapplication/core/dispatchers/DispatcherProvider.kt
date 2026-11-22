package com.rudra.web3tasknotesapplication.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Lets tests control where repository writes run. */
fun interface DispatcherProvider {
    fun io(): CoroutineDispatcher
}

/** Provides the production dispatcher for database writes. */
object DefaultDispatcherProvider : DispatcherProvider {
    override fun io() = Dispatchers.IO
}
