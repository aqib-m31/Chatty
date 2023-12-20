package com.aqib.chatty

import android.app.Application
import com.aqib.chatty.di.AppContainer
import com.aqib.chatty.di.DefaultContainer

/**
 * The main application class for Chatty.
 * It initializes the dependency injection container.
 */
class ChattyApplication : Application() {
    /**
     * The dependency injection container for the application.
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultContainer(this)
    }
}