package com.free.music.tube.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.free.music.tube.models.Artist
import com.squareup.picasso.Picasso
import androidx.appcompat.app.AppCompatActivity
import com.free.music.tube.fragments.ArtistDetailFragment
import android.os.Bundle

class ArtistAdapter(private val mDataSet: List<Artist>, val context : AppCompatActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBinding =
            LayoutInflater.from(parent.context).inflate(com.free.music.tube.R.layout.item_artist, parent, false)

        return ViewHolder(viewBinding).listen { pos ->
            val item = mDataSet[pos]

            val fragment = ArtistDetailFragment()
            val fragmentManager = context.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val bundle = Bundle()
            bundle.putSerializable("name", item)
            fragment.arguments = bundle
            fragmentTransaction.replace(com.free.music.tube.R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val artist = mDataSet[position]
        holder as ViewHolder

        holder.mName.text = artist.name

        if (artist.imageFile.isNotEmpty()) {

            Picasso
                .with(holder.mImage.context)
                .load(artist.imageFile)
                .into(holder.mImage)

            println(artist.imageFile)
        }
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
    }
}