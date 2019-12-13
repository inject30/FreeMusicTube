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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.free.music.tube.R
import com.free.music.tube.api.DatabaseAccess
import com.free.music.tube.models.IModel

abstract class BaseFragment<T : IModel> : Fragment(), DatabaseAccess.ResponseListener<T> {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var mLayoutManager: GridLayoutManager
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    var mDataSet: MutableList<T> = ArrayList()
    private lateinit var mConnectionErrorContainer: View
    private lateinit var mSearchErrorContainer: View

    abstract fun createAdapter(): RecyclerView.Adapter<*>
    abstract fun createLayoutManager(): GridLayoutManager
    abstract fun getData()
    abstract fun getRows() : Int

    lateinit var mDatabaseAccess: DatabaseAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        mDatabaseAccess = DatabaseAccess(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_base, container, false)

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        mSwipeRefreshLayout.isEnabled = false

        val popUpButton = view.findViewById<ImageView>(R.id.btnPopUpToolbar)
        popUpButton.setOnClickListener { p0 ->
            val popupMenu = PopupMenu(p0.context, p0)
            popupMenu.inflate(R.menu.menu_popup_toolbar)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popup_privacy_policy -> {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.privacy_policy_url)))
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

        val errorIcon = view.findViewById<ImageView>(R.id.error_icon)
        errorIcon.setBackgroundResource(R.drawable.ic_cloud_off_black_24dp)

        mConnectionErrorContainer = view.findViewById(R.id.error_container)
        mConnectionErrorContainer.setOnClickListener {
            mConnectionErrorContainer.visibility = View.GONE
            mSearchErrorContainer.visibility = View.GONE
            mSwipeRefreshLayout.isRefreshing = true
            getData()
        }

        val searchErrorIcon = view.findViewById<ImageView>(R.id.search_error_icon)
        searchErrorIcon.setBackgroundResource(R.drawable.ic_error_outline_black_24dp)

        mSearchErrorContainer = view.findViewById(R.id.search_error_container)
        mSearchErrorContainer.setOnClickListener {
            mConnectionErrorContainer.visibility = View.GONE
            mSearchErrorContainer.visibility = View.GONE
            mSwipeRefreshLayout.isRefreshing = true
            getData()
        }

        mRecyclerView = view.findViewById(R.id.recycler_view)
        mLayoutManager = createLayoutManager()
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = createAdapter()
        mRecyclerView.adapter = mAdapter

        getData()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDataSet.clear()
        mAdapter.notifyDataSetChanged()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        mRecyclerView.stopScroll()
        mLayoutManager.spanCount = getRows()
        mLayoutManager.requestLayout()
    }

    override fun onSuccessResult(result: List<T>) {
        if (mSwipeRefreshLayout.isRefreshing) mSwipeRefreshLayout.isRefreshing = false;

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