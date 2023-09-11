package com.example.welisten.myLibrary.adapter
import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.welisten.myLibrary.models.SongModel
import com.example.welisten.R
import com.example.welisten.clickListnersInterface.ClickListener

class MyLibraryAdapter( private var clickListener: ClickListener) : RecyclerView.Adapter<MyLibraryAdapter.ViewHolder>() {

    private  var songs: List<SongModel> =ArrayList()


    fun setAdapter(songs: List<SongModel>){
        this.songs=songs

        notifyItemRangeChanged(0,songs.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

val view=LayoutInflater.from(parent.context).inflate(R.layout.song_model,parent,false)

        return ViewHolder(view,clickListener)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val itemsViewModel= songs[position]

        holder.song.text = itemsViewModel.songTitle

        holder.artist.text = itemsViewModel.songArtist

        Glide.with(holder.itemView.context).asBitmap().placeholder(R.drawable.music).fitCenter().load(itemsViewModel.albumImage).into(holder.image)

        if (songs[position].isPlaying){
        holder.song.setTextColor("#2196F3".toColorInt())

        }
        else{
            holder.song.setTextColor("#FFFFFF".toColorInt())

        }
    }

    override fun getItemCount(): Int {


        return songs.size


    }

    class ViewHolder(itemView: View, clickListener: ClickListener) :RecyclerView.ViewHolder(itemView) {
        val image :ImageView =itemView.findViewById(R.id.imageView)

        val song :TextView =itemView.findViewById(R.id.textView2)

        val artist :TextView =itemView.findViewById(R.id.textView3)
        init {
            itemView.setOnClickListener {
                clickListener.onclick(absoluteAdapterPosition)

            }
        }

    }
}