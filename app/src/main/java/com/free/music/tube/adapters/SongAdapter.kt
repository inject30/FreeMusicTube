package com.free.music.tube.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.free.music.tube.R
import com.free.music.tube.models.Song
import com.free.music.tube.player.AppPlayer

class SongAdapter(private val mDataSet: List<Song>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBinding = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)

        return ViewHolder(viewBinding).listen { position ->
            val song = mDataSet[position]

            AppPlayer(viewBinding.context, song).create()
        }
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val song = mDataSet[position]
        holder as ViewHolder

        holder.mName.text =  song.title
        holder.mArtist.text =  song.artist
        holder.mDuration.text = song.durationString
        holder.mPopUp.visibility = View.GONE

        if (song.albumArt.isNotEmpty()) {
            Picasso
                .with(holder.mImage.context)
                .load(song.albumArt)
                .into(holder.mImage)
        }
    }

    private fun <Song : RecyclerView.ViewHolder> Song.listen(event: (position: Int) -> Unit): Song {
        itemView.setOnClickListener {
            event.invoke(adapterPosition)
        }
        return this
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage: ImageView = itemView.findViewById(R.id.image)
        var mName: TextView = itemView.findViewById(R.id.name)
        var mArtist: TextView = itemView.findViewById(R.id.artist)
        var mDuration: TextView = itemView.findViewById(R.id.duration)
        var mPopUp: ImageView = itemView.findViewById(R.id.popupMenu)
    }
}