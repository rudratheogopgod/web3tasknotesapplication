package com.rudra.web3tasknotesapplication.core.time

/** Supplies epoch milliseconds so save rules can be tested with a fixed clock. */
fun interface TimeProvider {
    fun nowMillis(): Long
}

/** Reads the wall clock for persisted timestamps. */
object SystemTimeProvider : TimeProvider {
    override fun nowMillis() = System.currentTimeMillis()
}
