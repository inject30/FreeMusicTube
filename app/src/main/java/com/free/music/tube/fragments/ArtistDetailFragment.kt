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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.free.music.tube.R
import com.free.music.tube.adapters.TrackAdapter
import com.free.music.tube.api.DatabaseAccess
import com.free.music.tube.models.Artist
import com.free.music.tube.models.Track
import com.squareup.picasso.Picasso

class ArtistDetailFragment : Fragment(), DatabaseAccess.ResponseListener<Track> {
    private lateinit var mArtist: Artist

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var mLayoutManager: GridLayoutManager
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var mDataSet = ArrayList<Track>()
    private lateinit var mConnectionErrorContainer: View
    private lateinit var mSearchErrorContainer: View

    private lateinit var mDatabaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

        mArtist = arguments!!.getSerializable("name") as Artist
        mDatabaseAccess = DatabaseAccess(activity!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_artist, container, false)

        val artistName: TextView = view.findViewById(R.id.artist_name)
        artistName.text = mArtist.name

        val artistImage: ImageView = view.findViewById(R.id.image)
        if (mArtist.imageFile.isNotEmpty()) {
            Picasso
                .with(activity)
                .load(mArtist.imageFile)
                .into(artistImage)
        }

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        mSwipeRefreshLayout.isEnabled = false

        mConnectionErrorContainer = view.findViewById(R.id.error_container)
        mConnectionErrorContainer.setOnClickListener {
            mConnectionErrorContainer.visibility = View.GONE
            mSearchErrorContainer.visibility = View.GONE
            mSwipeRefreshLayout.isRefreshing = true
            mDatabaseAccess.showArtistTracks(mArtist,this)
        }

        val searchErrorIcon = view.findViewById<ImageView>(R.id.search_error_icon)
        searchErrorIcon.setBackgroundResource(R.drawable.ic_error_outline_black_24dp)

        mSearchErrorContainer = view.findViewById(R.id.search_error_container)
        mSearchErrorContainer.setOnClickListener {
            mConnectionErrorContainer.visibility = View.GONE
            mSearchErrorContainer.visibility = View.GONE
            mSwipeRefreshLayout.isRefreshing = true
            mDatabaseAccess.showArtistTracks(mArtist,this)
        }

        val popUpButton = view.findViewById<ImageView>(R.id.btnPopUpToolbar)
        popUpButton.setOnClickListener { p0 ->
            val popupMenu = PopupMenu(p0.context, p0)
            popupMenu.inflate(R.menu.menu_popup_artist)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popup_artist_website -> {
                        if(!mArtist.website.isNullOrEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mArtist.website))
                            startActivity(intent)
                        } else{
                            Toast.makeText(activity, "Not Found Artist Website", Toast.LENGTH_SHORT).show()
                        }

                        true
                    }
                    R.id.popup_artist_page -> {
                        if(!mArtist.url.isNullOrEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mArtist.url))
                            startActivity(intent)
                        } else{
                            Toast.makeText(activity, "Not Found Artist Url", Toast.LENGTH_SHORT).show()
                        }

                        true
                    }
                    R.id.popup_artist_wiki -> {
                        if(!mArtist.wikipedia.isNullOrEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mArtist.wikipedia))
                            startActivity(intent)
                        } else{
                            Toast.makeText(activity, "Not Found Artist Wiki Page", Toast.LENGTH_SHORT).show()
                        }

                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }


        val backButton = view.findViewById<ImageView>(R.id.btnBack)
        backButton.setOnClickListener { activity!!.onBackPressed() }

        mRecyclerView = view.findViewById(R.id.recycler_view)
        mLayoutManager = GridLayoutManager(activity, getRows())
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = TrackAdapter(mDataSet)
        mRecyclerView.adapter = mAdapter

        mDatabaseAccess.showArtistTracks(mArtist,this)
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

    override fun onSuccessResult(result: List<Track>) {
        mSwipeRefreshLayout.isRefreshing = false
        mDataSet.addAll(result)
        mDataSet.shuffle()
        mAdapter.notifyDataSetChanged()

        if (mDataSet.isEmpty()) mSearchErrorContainer.visibility = View.VISIBLE
    }

    override fun onError() {
        mSwipeRefreshLayout.isRefreshing = false
        if (mDataSet.isEmpty()) mConnectionErrorContainer.visibility = View.VISIBLE
    }
}