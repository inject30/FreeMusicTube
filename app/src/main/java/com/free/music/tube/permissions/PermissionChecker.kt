package com.free.music.tube.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * An utility that checks the current application's dangerous permissions at runtime.
 */
interface PermissionChecker {
    /** Whether files stored on the device's external storage can be read by this application. */
    val canReadExternalStorage: Boolean
    /** Whether this application can write to files stored on the external storage. */
    val canWriteToExternalStorage: Boolean
}

/**
 * Real implementation of the [PermissionChecker].
 * This checks permission using the provided [application context][context].
 */
class PermissionCheckerImpl(private val context: Context) : PermissionChecker {

    override val canReadExternalStorage: Boolean
        get() = isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
    override val canWriteToExternalStorage: Boolean
        get() = isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private fun isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}