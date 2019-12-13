package com.free.music.tube.utils

import android.os.Environment

object FileUtils {

    fun isStorageCanWrite(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}
