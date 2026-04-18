package com.operit.plugin

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.operit.plugin.server.OperitApiService

/**
 * Application class for Operit Plugin
 * Auto-starts API service on app launch
 */
class OperitPluginApplication : Application() {
    
    companion object {
        private const val TAG = "OperitPluginApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Operit Plugin Application initialized")
    }
}