package com.smascaro.trackmixing.common.utils

import android.os.Environment
import android.os.StatFs
import javax.inject.Inject

class DiskSpaceHelper @Inject constructor() {
    fun getAvailableBytes(): Long {
        return StatFs(Environment.getDataDirectory().path).availableBytes
    }
}