package com.rudra.web3tasknotesapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Owns the application-wide graph for the local notes store. */
@HiltAndroidApp
class Web3TaskNotesApp : Application()
