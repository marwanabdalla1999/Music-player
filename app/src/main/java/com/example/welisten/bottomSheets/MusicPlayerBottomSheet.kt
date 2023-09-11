package com.example.welisten.bottomSheets

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.welisten.myLibrary.models.SongModel
import com.example.welisten.R
import com.example.welisten.mainActivity.Home
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MusicPlayerBottomSheet : BottomSheetDialogFragment(), OnClickListener, Runnable,
    Palette.PaletteAsyncListener {
    private lateinit var songProgress: SeekBar
    private lateinit var songCover: ImageView
    private lateinit var songTitle: TextView
    private lateinit var songDesc: TextView
    private lateinit var next: ImageView
    private lateinit var previous: ImageView
    private lateinit var playPause: ImageView
    private lateinit var back: ImageView
    private lateinit var handler: Handler
    private lateinit var background: ConstraintLayout
    private lateinit var totalTime: TextView
    private lateinit var currentTime: TextView
    private var position: Int = -1
    private lateinit var listOfSongs: List<SongModel>
    private lateinit var rotation: Animation


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        handler.removeCallbacks(this)

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        handler.removeCallbacks(this)
    }

    override fun getTheme() = R.style.CustomBottomSheetDialogTheme


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.music_player_bootm_sheet, container, false)

        insIt(view)

        getData()
        setListeners()
        setData()

        return view

    }

    private fun setListeners() {

        back.setOnClickListener(this)

        playPause.setOnClickListener(this)

        next.setOnClickListener(this)

        previous.setOnClickListener(this)

        songProgress.setOnSeekBarChangeListener(onSeekBarChanged())

    }

    private fun insIt(view: View) {
        songProgress = view.findViewById(R.id.songProgress)
        songCover = view.findViewById(R.id.SongCover)
        songTitle = view.findViewById(R.id.songTitle)
        songDesc = view.findViewById(R.id.songDesc)
        back = view.findViewById(R.id.back)
        playPause = view.findViewById(R.id.playPause)
        next = view.findViewById(R.id.next)
        previous = view.findViewById(R.id.Previous)
        background = view.findViewById(R.id.background)
        totalTime = view.findViewById(R.id.total_time)
        currentTime = view.findViewById(R.id.current_time)
        handler = Handler(Looper.getMainLooper())
        rotation = AnimationUtils.loadAnimation(songCover.context, R.anim.rotate)


    }

    private fun getData() {

        position = arguments?.getInt("Position")!!
        val gson = Gson()
        val jsonString = arguments?.getString("listOfSongs")
        val token = object : TypeToken<ArrayList<SongModel>>() {}.type
        listOfSongs = gson.fromJson(jsonString, token)


    }


    private fun onSeekBarChanged(): SeekBar.OnSeekBarChangeListener {

        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    (activity as Home).seekSong((p1 * 1000).toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        }
    }

    private fun getCustomTarget(): CustomTarget<Bitmap> {
        return object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                songCover.setImageBitmap(resource)
                Palette.from(resource).generate(this@MusicPlayerBottomSheet)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                songCover.setImageResource(R.drawable.music)
                background.background.setTint(
                    ContextCompat.getColor(
                        background.context, R.color.gray
                    )
                )
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }

        }

    }

    private fun setData() {

        val duration = Integer.parseInt(listOfSongs[position].duration) / 1000

        Glide.with(songCover.context).asBitmap().load(listOfSongs[position].albumImage)
            .into(getCustomTarget())

        songTitle.text = listOfSongs[position].songTitle

        songDesc.text = listOfSongs[position].songArtist

        songProgress.max = duration

        totalTime.text = convertToTimeFormat(duration)

        songCover.startAnimation(rotation)

        handler.post(this)

    }

    private fun convertToTimeFormat(duration: Int): String {
        var min = 0
        var sec = duration
        while (sec > 60) {

            sec -= 60
            min++

        }
        var time = ""
        time += if (min <= 9) {
            "0$min:"
        } else {
            "$min:"

        }
        time += if (sec <= 9) {
            "0$sec"

        } else {
            "$sec"

        }
        return time
    }

    fun onCompletionPlay() {

        handler.removeCallbacks(this)

        songProgress.progress = 0

        ++position

        setData()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BottomSheetBehavior.from(view.parent as View).state = BottomSheetBehavior.STATE_EXPANDED

        background.minimumHeight = Resources.getSystem().displayMetrics.heightPixels


    }

    private fun animateColorValue(background: ConstraintLayout, color: Int) {

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), background.solidColor, color)
        colorAnimation.duration = 500L
        colorAnimation.addUpdateListener { animator -> background.background.setTint(animator.animatedValue as Int) }
        colorAnimation.start()

    }


    override fun run() {

        val currentPosition = (activity as Home).getSongCurrentPosition()
        if ((activity as Home).isPlaying()) {
            playPause.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        }
        songProgress.progress = currentPosition
        val time = convertToTimeFormat(currentPosition)
        currentTime.text = time

        handler.postDelayed(this, 100)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.playPause -> (activity as Home).resumePause()

            R.id.back -> dismiss()

            R.id.next -> (activity as Home).playNext()


            R.id.Previous -> (activity as Home).playPrev()


        }
    }


    override fun onGenerated(palette: Palette?) {
        if (palette != null) {
            val color = palette.getDominantColor(0)
            animateColorValue(background, color)
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight
        }
    }

}