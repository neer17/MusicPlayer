package com.example.musicplayer.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.SongData


class ListSongsAdapter(
    private val songsList: ArrayList<SongData>,
    private val songClickListenerInstance: SongClickListener
) :
    RecyclerView.Adapter<ListSongsAdapter.ViewHolder>() {
    private val TAG = ListSongsAdapter::class.java.simpleName

    lateinit var parent: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: size ==> ${this.songsList.size}")
        return this.songsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = this.songsList[position]
        val source = currentItem.source
        val image = currentItem.image
        val title = currentItem.title
        val artist = currentItem.artist

        holder.bind(image, title, artist)
        holder.itemView.setOnClickListener {
            songClickListenerInstance.onClick(SongData(title, artist, image, source), position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.song_cover_image_view)
        private val titleView: TextView = itemView.findViewById(R.id.song_text_view)
        private val artistTextView: TextView = itemView.findViewById(R.id.artist_text_view)

        fun bind(image: Any?, title: String, artist: String) {
            titleView.text = title
            artistTextView.text = artist

            if (image != null)
                Glide
                    .with(itemView)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.spinner_animation)
                    .into(imageView)
            else Glide
                .with(itemView)
                .load(getDrawable(parent.context, R.drawable.record_image_50_50))
                .into(imageView)
        }
    }

    class SongClickListener(val clickListener: (songData: SongData, position: Int) -> Unit) {
        fun onClick(songData: SongData, position: Int) = clickListener(songData, position)
    }
}