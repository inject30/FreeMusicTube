package com.free.music.tube.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.free.music.tube.R
import com.free.music.tube.fragments.ConfirmDialogFragment
import com.free.music.tube.fragments.LibraryFragment
import com.free.music.tube.permissions.READ_EXTERNAL_STORAGE_REQUEST
import com.free.music.tube.permissions.WRITE_EXTERNAL_STORAGE_REQUEST
import com.free.music.tube.player.AppPlayer

private const val ACTION_RANDOM = "com.free.music.tube.ACTION_RANDOM"

open class PermissionActivity : AppCompatActivity() {

    private var mPlayer: AppPlayer? = null

    fun createBothPlayer(player: AppPlayer?) {
        this.mPlayer = player
    }

    fun handleIntent(intent: Intent?): Boolean {
        when (intent?.action) {
            ACTION_RANDOM -> {
                // Intent is purposely marked as not handled to trigger home screen display
                return false
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED && ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {

                ConfirmDialogFragment.newInstance(
                    null,
                    0,
                    title = getString(R.string.permission_confirm_dialog),
                    message = getString(R.string.write_external_storage_permission_rationale),
                    positiveButton = R.string.ok
                ).show(supportFragmentManager, null)

            } else if (grantResults.isNotEmpty() && grantResults[0] == 0) {
                if (mPlayer != null) {
                    mPlayer!!.download(this)
                }
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            // Show an informative dialog message if permission is not granted and user has not checked "Don't ask again".
            if (grantResults[0] == PackageManager.PERMISSION_DENIED && ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                ConfirmDialogFragment.newInstance(
                    null,
                    0,
                    title = getString(R.string.permission_confirm_dialog),
                    message = getString(R.string.read_external_storage_permission_rationale),
                    positiveButton = R.string.ok
                ).show(supportFragmentManager, null)

            } else if (grantResults.isNotEmpty() && grantResults[0] == 0) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, LibraryFragment()).addToBackStack(null)
                    .commit()
            }
        } else {
            return
        }
    }
}
