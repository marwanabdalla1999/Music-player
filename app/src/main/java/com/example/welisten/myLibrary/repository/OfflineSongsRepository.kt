package com.example.welisten.myLibrary.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.welisten.myLibrary.models.SongModel
import com.example.welisten.const.Credentials

class OfflineSongsRepository {


    private var offlineSongsModelLiveData = MutableLiveData<List<SongModel>>()


    @RequiresApi(Build.VERSION_CODES.Q)
    fun call(context: Context) {

        getSongs(context)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getSongs(context: Context) {
        val songModels: ArrayList<SongModel> = ArrayList()

        val contentResolver: ContentResolver = context.contentResolver
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, null
        )


        if (cursor != null && cursor.moveToFirst()) {
            val songIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songDataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val songId = cursor.getLong(songIdIndex)
                val songTitle = cursor.getString(songTitleIndex)
                val songArtist = cursor.getString(songArtistIndex)
                val songData = cursor.getString(songDataIndex)
                val date = cursor.getLong(dateIndex)
                val albumId = cursor.getLong(albumIdIndex)
                val album = cursor.getString(albumIndex)
                val duration = cursor.getString(durationIndex)
                val imageUri = Uri.parse(Credentials.imageUrl)
                val albumUri = ContentUris.withAppendedId(imageUri, albumId)


                if (songData != null && songData.endsWith(".mp3")) {
                    songModels.add(
                        SongModel(
                            songId,
                            songTitle,
                            songArtist,
                            songData,
                            date,
                            albumUri.toString(),
                            album,
                            duration
                        )
                    )
                }


            }
            offlineSongsModelLiveData.value = songModels
            cursor.close()
        }
    }


    fun get(): MutableLiveData<List<SongModel>> {

        return offlineSongsModelLiveData
    }


}