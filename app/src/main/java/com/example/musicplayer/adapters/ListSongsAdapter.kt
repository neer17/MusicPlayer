package com.example.musicplayer.adapters

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.PlaySong
import com.example.musicplayer.R
import com.example.musicplayer.data.SongData
import java.io.ByteArrayOutputStream


class ListSongsAdapter(songsList: ArrayList<SongData>) :
    RecyclerView.Adapter<ListSongsAdapter.ViewHolder>() {
    private val TAG = ListSongsAdapter::class.java.simpleName

    lateinit var parent: ViewGroup
    private val list = songsList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, list)
        val currentItem = list[position]
        val source = currentItem.source
        val image = currentItem.image

//        Log.d(TAG, "onBindViewHolder: tpye of image ==> ${image is Bitmap}")

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlaySong::class.java)

            image.let {
                if (it is Bitmap) {
                    //Convert to byte array
                    val stream = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    intent.putExtra("IMAGE", byteArray)

                }
            }

            intent.putExtra("SOURCE", source)

            startActivity(holder.itemView.context, intent, null)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.song_cover_image_view)
        private val titleView: TextView = itemView.findViewById(R.id.song_text_view)
        private val artistTextView: TextView = itemView.findViewById(R.id.artist_text_view)

        fun bind(position: Int, list: ArrayList<SongData>) {
            val currentItem = list[position]
            val image: Any? = currentItem.image
            val title = currentItem.title
            val artist = currentItem.artist

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

    class SongClickListener(val clickListener: (songSource: String) -> Unit) {
        fun onClick(songSource: String) = clickListener(songSource)
    }
}