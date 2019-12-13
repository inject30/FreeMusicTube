package com.free.music.tube.application

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        requestQueue = Volley.newRequestQueue(this)
    }

    companion object {
        var requestQueue: RequestQueue? = null
            private set
    }
}