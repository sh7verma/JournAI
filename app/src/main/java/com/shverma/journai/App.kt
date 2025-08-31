package com.shverma.journai

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.shverma.journai.utils.GlobalResourceProvider
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application(){

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        GlobalResourceProvider.init(this)
        FirebaseApp.initializeApp(this)
    }
}
