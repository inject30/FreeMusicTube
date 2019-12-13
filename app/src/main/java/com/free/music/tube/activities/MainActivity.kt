package com.free.music.tube.activities

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.free.music.tube.R
import com.free.music.tube.fragments.LibraryFragment
import com.free.music.tube.fragments.HotMusicFragment
import com.free.music.tube.fragments.TrendingArtistsFragment
import com.free.music.tube.permissions.PermissionCheckerImpl
import com.free.music.tube.permissions.requestReadExternalStoragePermission

class MainActivity : PermissionActivity() {

    private lateinit var permissions: PermissionCheckerImpl

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_hot -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, HotMusicFragment()).addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_trending -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, TrendingArtistsFragment()).addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_music -> {
                if (permissions.canReadExternalStorage) {
                    if (!handleIntent(intent)) {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, LibraryFragment()).addToBackStack(null)
                            .commit()
                    }
                } else this.requestReadExternalStoragePermission()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.navigation)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        permissions = PermissionCheckerImpl(this)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, HotMusicFragment())
            .commit()
    }
}