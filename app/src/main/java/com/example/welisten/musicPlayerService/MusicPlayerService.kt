package com.example.welisten.musicPlayerService

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import com.example.welisten.myLibrary.onCompleteListener.OnCompleteListener
import java.io.IOException


class MusicPlayerService(
    private val context: Context?, private val onCompleteListener: OnCompleteListener
) : OnCompletionListener {


    private var musicPlayer = MediaPlayer()


    fun playMusic(path: String) {
        try {

            if (context != null) {
                musicPlayer.stop()
                musicPlayer.release()
                musicPlayer = MediaPlayer()
                musicPlayer.setOnCompletionListener(this)
                musicPlayer.setDataSource(context.applicationContext, Uri.parse(path))
                musicPlayer.prepareAsync()
                musicPlayer.setOnPreparedListener { mp ->
                    mp.start()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }


    fun resumePause() {
        if (musicPlayer.isPlaying) {
            musicPlayer.pause()
        } else {
            musicPlayer.start()
        }
    }

    fun getSongCurrentPosition(): Int {

        return musicPlayer.currentPosition / 1000

    }

    override fun onCompletion(p0: MediaPlayer?) {

        onCompleteListener.onComplete()

    }

    fun isPlaying(): Boolean {
        return musicPlayer.isPlaying
    }

    fun seekSongToPosition(position: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            musicPlayer.seekTo(position, MediaPlayer.SEEK_CLOSEST)
    }
        else musicPlayer.seekTo(position.toInt())
    }

    fun playNext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            musicPlayer.seekTo((musicPlayer.duration*1000).toLong(), MediaPlayer.SEEK_CLOSEST)
        }
        else musicPlayer.seekTo(musicPlayer.duration*1000)
    }

    fun playPrev() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            musicPlayer.seekTo((0).toLong(), MediaPlayer.SEEK_CLOSEST)
        }
        else musicPlayer.seekTo(0)
    }


}