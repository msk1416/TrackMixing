package com.smascaro.trackmixing.service.common

import android.app.Service
import android.content.Intent
import android.os.IBinder

open class BaseService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}