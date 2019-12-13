package com.free.music.tube.fragments

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.free.music.tube.R
import com.free.music.tube.adapters.SongAdapter
import com.free.music.tube.api.SongHandler
import com.free.music.tube.models.Song

class LibraryFragment : Fragment() {


    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mSongAdapter: SongAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mSearchErrorContainer: View
    private lateinit var mErrorIcon: ImageView
    private lateinit var mErrorRetry: TextView
    private lateinit var mErrorMessage: TextView
    private lateinit var mErrorMessage2: TextView
    private lateinit var mDataSet: List<Song>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_base, container, false)

        mDataSet = SongHandler.getAllSongs(activity!!.contentResolver)
        mRecyclerView = view.findViewById(R.id.recycler_view)
        mGridLayoutManager = GridLayoutManager(activity, getRows())
        mRecyclerView.layoutManager = mGridLayoutManager
        mSongAdapter = SongAdapter(mDataSet)
        mRecyclerView.adapter = mSongAdapter

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.isEnabled = false

        mSearchErrorContainer = view.findViewById(R.id.error_container)
        mErrorIcon = view.findViewById(R.id.error_icon)
        mErrorRetry = view.findViewById(R.id.error_retry)
        mErrorMessage = view.findViewById(R.id.error_message)
        mErrorMessage2 = view.findViewById(R.id.error_message2)

        val popUpButton = view.findViewById<ImageView>(R.id.btnPopUpToolbar)
        popUpButton.setOnClickListener { p0 ->
            val popupMenu = PopupMenu(p0.context, p0)
            popupMenu.inflate(R.menu.menu_popup_toolbar)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popup_privacy_policy -> {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(resources.getString(R.string.privacy_policy_url))
                        )
                        startActivity(browserIntent)
                        true
                    }
                    R.id.popup_feedback -> {
                        val eMail = Intent(Intent.ACTION_SEND)
                        eMail.type = resources.getString(R.string.email_type)
                        eMail.putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.email_url)))
                        eMail.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.feedback_email))
                        eMail.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.email_dear) + "")
                        startActivity(Intent.createChooser(eMail, resources.getString(R.string.send_feedback)))
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        val searchButton = view.findViewById<ImageView>(R.id.btnSearch)
        searchButton.setOnClickListener {
            val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, SearchFragment.newInstance()).addToBackStack(null).commit()
        }

        if (mDataSet.isEmpty()) {
            mSearchErrorContainer.visibility = View.VISIBLE
            mErrorIcon.setBackgroundResource(R.drawable.ic_no_songs_found_black_24dp)
            mErrorRetry.visibility = View.GONE
            mErrorMessage.text = resources.getString(R.string.tracks_not_found)
            mErrorMessage2.text = resources.getString(R.string.tracks_not_found2)
        }


        return view
    }

    private fun getRows(): Int {
        val rows: Int
        val configuration = activity!!.resources.configuration
        rows = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            2
        else
            1

        return rows
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        mRecyclerView.stopScroll()
        mGridLayoutManager.spanCount = getRows()
        mGridLayoutManager.requestLayout()
    }

}