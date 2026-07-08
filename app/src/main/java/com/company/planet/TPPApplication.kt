package com.company.planet

import android.app.Application
import com.google.firebase.FirebaseApp

class TPPApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
