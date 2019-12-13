package com.free.music.tube.fragments

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.free.music.tube.adapters.ArtistAdapter
import com.free.music.tube.models.Artist

class TrendingArtistsFragment : BaseFragment<Artist>() {

    override fun getRows(): Int {
        val rows: Int
        val configuration = activity!!.resources.configuration
        rows = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            3
        else
            2

        return rows
    }

    override fun createLayoutManager(): GridLayoutManager {
        return GridLayoutManager(activity, getRows())
    }

    override fun getData() {
        mDatabaseAccess.showArtists(this)
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ArtistAdapter(mDataSet, activity as AppCompatActivity)
    }
}