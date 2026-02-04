package com.mavilon.mongomovies

import android.app.Application
import com.mavilon.mongomovies.di.AppContainer

class MongoMoviesApp : Application() {
    lateinit var container: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
