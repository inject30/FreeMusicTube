package com.free.music.tube.fragments

import android.content.res.Configuration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.free.music.tube.adapters.TrackAdapter
import com.free.music.tube.models.Track

class HotMusicFragment : BaseFragment<Track>() {
    override fun getRows(): Int {
        val rows: Int
        val configuration = activity!!.resources.configuration
        rows = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            2
        else
            1

        return rows
    }

    override fun createLayoutManager(): GridLayoutManager {
        return GridLayoutManager(activity, getRows())
    }

    override fun getData() {
        mSwipeRefreshLayout.isRefreshing = true
        mDatabaseAccess.showsTracks(this)

    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return TrackAdapter(mDataSet)
    }
}