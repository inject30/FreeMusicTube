package com.free.music.tube.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.free.music.tube.models.Track
import com.free.music.tube.player.AppPlayer

class TrackAdapter(private val mDataSet: List<Track>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBinding =
            LayoutInflater.from(parent.context).inflate(com.free.music.tube.R.layout.item_track, parent, false)

        return ViewHolder(viewBinding).listen { pos ->
            val item = mDataSet[pos]

            AppPlayer(viewBinding.context, item).create()
        }
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val track = mDataSet[position]
        holder as ViewHolder

        holder.mName.text = track.trackTitle
        holder.mArtist.text = track.artistName
        holder.mDuration.text = track.trackDuration

        if (track.trackImageFile.isNotEmpty()) {

            Picasso
                .with(holder.mImage.context)
                .load(track.trackImageFile)
                .into(holder.mImage)

            println(track.trackImageFile)
        }

        holder.mPopUp.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(com.free.music.tube.R.menu.menu_popup_track)
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        when (item.itemId) {
                            com.free.music.tube.R.id.popup_track_goto_artist_page -> {
                                if (track.artistUrl.isNotEmpty()) {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(track.artistUrl))
                                    view.context.startActivity(browserIntent)
                                } else {
                                    Toast.makeText(
                                        view.context,
                                        com.free.music.tube.R.string.artist_no_url,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                return true
                            }
                            com.free.music.tube.R.id.popup_track_goto_track_page -> {
                                if (track.trackUrl.isNotEmpty()) {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(track.trackUrl))
                                    view.context.startActivity(browserIntent)
                                } else {
                                    Toast.makeText(
                                        view.context,
                                        com.free.music.tube.R.string.track_no_url,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                return true
                            }
                            com.free.music.tube.R.id.popup_track_cc_license -> {
                                if (track.licenseUrl.isNotEmpty()) {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(track.licenseUrl))
                                    view.context.startActivity(browserIntent)
                                } else {
                                    Toast.makeText(
                                        view.context,
                                        com.free.music.tube.R.string.no_license_url,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                return true
                            }
                            else -> return false
                        }
                    }
                })

                popupMenu.show()
            }
        })
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition)
        }
        return this
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage: ImageView = itemView.findViewById(com.free.music.tube.R.id.image)
        var mName: TextView = itemView.findViewById(com.free.music.tube.R.id.name)
        var mArtist: TextView = itemView.findViewById(com.free.music.tube.R.id.artist)
        var mDuration: TextView = itemView.findViewById(com.free.music.tube.R.id.duration)
        var mPopUp: ImageView = itemView.findViewById(com.free.music.tube.R.id.popupMenu)
    }
}