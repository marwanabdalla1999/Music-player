package com.example.welisten.mainActivity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.welisten.myLibrary.models.SongModel
import com.example.welisten.R
import com.example.welisten.bottomSheets.MusicPlayerBottomSheet
import com.example.welisten.endPoints.APIEndpoints
import com.example.welisten.musicPlayerService.MusicPlayerService
import com.example.welisten.myLibrary.onCompleteListener.OnCompleteListener
import com.example.welisten.myLibrary.ui.MyLibrary
import com.example.welisten.network.Client
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Home : AppCompatActivity(), NavigationBarView.OnItemSelectedListener, OnCompleteListener,
    Runnable, OnClickListener, Palette.PaletteAsyncListener {


    private lateinit var musicBar: ConstraintLayout
    private lateinit var musicPlayerBottomSheet: MusicPlayerBottomSheet
    private lateinit var handler: Handler
    private lateinit var client: APIEndpoints
    private lateinit var musicProgress: ProgressBar
    private lateinit var musicCover: ImageView
    private lateinit var playPauseButton: ImageView
    private lateinit var musicName: TextView
    private lateinit var musicDesc: TextView
    private var position: Int = -1
    private var prevPosition: Int = -1
    private lateinit var songsList: List<SongModel>
    private val library = MyLibrary()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = library

    private lateinit var musicPlayerService: MusicPlayerService


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inIt()
        startFragmentTransaction()

    }

    private fun startFragmentTransaction() {
        fragmentManager.beginTransaction().apply {
            add(R.id.container, library, "Library")
        }.commit()
    }


    private fun inIt() {

        musicBar = findViewById(R.id.music_bar)
        client = Client.getInstance().create(APIEndpoints::class.java)
        musicProgress = findViewById(R.id.musicProgress)
        musicCover = findViewById(R.id.musicCover)
        musicName = findViewById(R.id.musicName)
        musicDesc = findViewById(R.id.musicDesc)
        playPauseButton = findViewById(R.id.playPauseButton)
        musicPlayerBottomSheet = MusicPlayerBottomSheet()
        handler = Handler(Looper.getMainLooper())
        musicPlayerService = MusicPlayerService(this, this)
        playPauseButton.setOnClickListener(this)
        musicBar.setOnClickListener(this)


    }

    fun play(songModels: List<SongModel>, position: Int) {
        songsList = songModels
        if (position != this.position) {
            this.position = position
            playSong()
        }
        showMusicPlayerScreen()

    }


    private fun playSong() {

        config()

        val duration = Integer.parseInt(songsList[position].duration) / 1000
        musicPlayerService.playMusic(songsList[position].songData)

        musicProgress.max = duration
        musicName.text = songsList[position].songTitle
        musicDesc.text = songsList[position].songArtist

        Glide.with(this).asBitmap().load(songsList[position].albumImage)
            .placeholder(R.drawable.music).into(getCustomTarget())

        handler.post(this)

    }

    private fun config() {
        if (prevPosition!=-1){
            songsList[prevPosition].isPlaying=false
        }
        songsList[position].isPlaying=true
        library.notifyItemChange(prevPosition)
        library.notifyItemChange(position)
        prevPosition=position

        musicBar.visibility = VISIBLE

    }

    private fun getCustomTarget(): CustomTarget<Bitmap> {
        return object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                musicCover.setImageBitmap(resource)
                Palette.from(resource).generate(this@Home)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                musicCover.setImageResource(R.drawable.music)
                musicBar.background.setTint(ContextCompat.getColor(this@Home, R.color.gray))
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }

        }

    }


    private fun loadFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(activeFragment).show(fragment)
        transaction.commit()
        return true
    }


    private fun showMusicPlayerScreen() {


        val bundle = Bundle()
        val gson = Gson()
        val jsonString = gson.toJson(songsList)
        bundle.putString("listOfSongs", jsonString)
        bundle.putInt("Position", position)
        musicPlayerBottomSheet.arguments = bundle
        musicPlayerBottomSheet.show(supportFragmentManager, "TAG")


    }


    private fun animateColorValue(background: ConstraintLayout, color: Int) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), background.solidColor, color)
        colorAnimation.duration = 500L
        colorAnimation.addUpdateListener { animator ->

            background.background.setTint(animator.animatedValue as Int)
        }
        colorAnimation.start()
        background.background.alpha = 100

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.myLibrary -> loadFragment(library)


            else -> false
        }
    }

    fun resumePause() {
        musicPlayerService.resumePause()
    }

    fun getSongCurrentPosition(): Int {

        return musicPlayerService.getSongCurrentPosition()
    }

    override fun onComplete() {
        handler.removeCallbacks(this)
        musicProgress.progress = 0
        if (position < songsList.size - 1 && position >= 0) {
            ++position
            playSong()
            musicPlayerBottomSheet.onCompletionPlay()
        }


    }

    override fun run() {
        val currentPosition = getSongCurrentPosition()
        if (isPlaying()) {
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        }
        musicProgress.progress = currentPosition

        handler.postDelayed(this, 100)


    }

    fun isPlaying(): Boolean {

        return musicPlayerService.isPlaying()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.playPauseButton -> {
                resumePause()
            }

            R.id.music_bar -> {
                showMusicPlayerScreen()
            }
        }
    }

    override fun onGenerated(palette: Palette?) {

        if (palette != null) {

            val color = palette.getDominantColor(0)
            animateColorValue(musicBar, color)
        }
    }

    fun seekSong(position: Long) {

        musicPlayerService.seekSongToPosition(position)
    }


    fun playNext() {
        musicPlayerService.playNext()
    }

    fun playPrev() {
        musicPlayerService.playPrev()
    }
}