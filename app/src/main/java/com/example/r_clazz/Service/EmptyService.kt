package com.example.r_clazz.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class EmptyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
