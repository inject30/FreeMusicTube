package com.free.music.tube.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.free.music.tube.R
import com.free.music.tube.adapters.TrackAdapter
import com.free.music.tube.api.DatabaseAccess
import com.free.music.tube.models.Track
import java.lang.NullPointerException
import kotlin.collections.ArrayList

class SearchFragment : Fragment(), DatabaseAccess.ResponseListener<Track> {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mTracksAdapter: TrackAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mSearchErrorContainer: View
    private var mDataSet = ArrayList<Track>()
    private lateinit var mQuery: String
    private lateinit var mDatabaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

        mDatabaseAccess = DatabaseAccess(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        mSwipeRefreshLayout.isEnabled = false

        mRecyclerView = view.findViewById(R.id.recycler_view)
        mGridLayoutManager = GridLayoutManager(activity, getRows())
        mTracksAdapter = TrackAdapter(mDataSet)
        mRecyclerView.adapter = mTracksAdapter

        val searchErrorIcon = view.findViewById<ImageView>(R.id.search_error_icon)
        searchErrorIcon.setBackgroundResource(R.drawable.ic_error_outline_black_24dp)

        mSearchErrorContainer = view.findViewById(R.id.search_error_container)
        mSearchErrorContainer.setOnClickListener {
            mSearchErrorContainer.visibility = View.GONE
            mSwipeRefreshLayout.isRefreshing = true
            search()
        }

        val searchButton = view.findViewById<EditText>(R.id.etSearch)
        searchButton.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                mQuery = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        searchButton.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                search()

                try {
                    val inputMethodManager =
                        activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(searchButton.windowToken, 0)
                } catch (npe: NullPointerException) {
                    npe.printStackTrace()
                }

            }
            false
        }

        val backButton = view.findViewById<ImageView>(R.id.btnBack)
        backButton.setOnClickListener { activity!!.onBackPressed() }

        return view
    }

    // TODO
    private fun search() {
        if (mDataSet.isNotEmpty()) mDataSet.clear()
        mTracksAdapter.notifyDataSetChanged()
        mSwipeRefreshLayout.isRefreshing = true
        mDatabaseAccess.searchTracks(mQuery, this)

    }

    override fun onSuccessResult(dataSet: MutableList<Track>) {
        mSwipeRefreshLayout.isRefreshing = false

        mDataSet.addAll(dataSet)
        mDataSet.shuffle()
        mTracksAdapter.notifyDataSetChanged()
        mRecyclerView.scrollToPosition(1)

        if (mDataSet.isEmpty()) mSearchErrorContainer.visibility = View.VISIBLE
    }
    override fun onError() {
        mSwipeRefreshLayout.isRefreshing = false
        if (mDataSet.isEmpty()) mSearchErrorContainer.visibility = View.VISIBLE
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

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }

    }
}
