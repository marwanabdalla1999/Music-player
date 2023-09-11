package com.example.welisten.myLibrary.models

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SongModel(

    val songId: Long,

    val songTitle: String,

    val songArtist: String,


    val songData: String,

    val date: Long,

    val albumImage: String,

    val album: String,


    val duration: String,

    ){

    var isPlaying=false
}

