package com.example.modacircularra.classes

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.modacircularra.viewModel.CarritoViewModel
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class MyApplication : Application() {

    val carritoViewModel: CarritoViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(CarritoViewModel::class.java)
    }

    override fun onCreate() {
        super.onCreate()

        try {
            FacebookSdk.setClientToken("EAAIvBCB68pQBOZB3d91Iq6AX8fUvYekX424hHSG4Xp1mhgg1wPy3WZAxXZCQC67BpZCK5vOO5AlTPsSnjY3byLIhiqqgqLQHtC5VG8I3JZA7VCdImbGcvRVarxbbtZAliSDVjq8myPA1jAMf8wcKZAhPi1ZBuVyXmj8dpYWSgDCvoGelcZBDBV0UyB3cn86oLaGVnuSN6Onl3sZCsqPVGnwkEzCt8yYuZAwPmrLXGbq4iDT0vjF701hbURttRt7LJAQBSgW6qVVD0hq7ZANr")
            // Initialize Facebook SDK
            FacebookSdk.sdkInitialize(this)
            AppEventsLogger.activateApp(this)
        } catch (e: Exception) {
            e.printStackTrace()  // Imprime la excepción si ocurre
        }
    }
}


