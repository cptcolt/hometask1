package ru.skillbranch.gameofthrones

import android.app.Application
import android.content.Context

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        App.appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }
}