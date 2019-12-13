package com.free.music.tube.permissions

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

const val READ_EXTERNAL_STORAGE_REQUEST = 98
const val WRITE_EXTERNAL_STORAGE_REQUEST = 99

/**
 * Issue a request to grant permission to read/write the device's external storage.
 * The result will be dispatched to [Activity.onRequestPermissionsResult]
 * with the request code [EXTERNAL_STORAGE_REQUEST].
 */
fun Activity.requestReadExternalStoragePermission() {
    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
}

fun Activity.requestWriteExternalStoragePermission() {
    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE_REQUEST)
}