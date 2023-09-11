package com.example.welisten.myLibrary.ui


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.welisten.myLibrary.models.SongModel
import com.example.welisten.R
import com.example.welisten.myLibrary.adapter.MyLibraryAdapter
import com.example.welisten.clickListnersInterface.ClickListener
import com.example.welisten.mainActivity.Home
import com.example.welisten.myLibrary.viewModel.OfflineSongsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyLibrary : Fragment(), ClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var musicBar: ConstraintLayout
    private lateinit var musicProgress: ProgressBar
    private lateinit var musicCover: ImageView
    private lateinit var playPauseButton: ImageView
    private val offlineSongsViewModel: OfflineSongsViewModel by viewModels()
    private lateinit var musicName: TextView
    private lateinit var musicDesc: TextView
    private lateinit var adapter: MyLibraryAdapter
    private lateinit var songs: List<SongModel>

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_my_library, container, false)


        insIt(view)

        getSongs()

        setUpRecyclerView()


        observeChanges()



        return view
    }

    private fun setUpRecyclerView() {
        adapter = MyLibraryAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getSongs() {
        offlineSongsViewModel.getSongs(recyclerView.context)

    }

    private fun insIt(view: View) {
        recyclerView = view.findViewById(R.id.searchRecyclerView)
        progress = view.findViewById(R.id.progress)
        musicBar = view.findViewById(R.id.music_bar)
        musicProgress = view.findViewById(R.id.musicProgress)
        musicCover = view.findViewById(R.id.musicCover)
        musicName = view.findViewById(R.id.musicName)
        musicDesc = view.findViewById(R.id.musicDesc)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        //offlineSongsViewModel = ViewModelProvider(this)[OfflineSongsViewModel::class.java]
    }

    private fun observeChanges() {
        offlineSongsViewModel.observeSongsLiveData().observe(viewLifecycleOwner) { data ->

            if (data != null) {
                songs=data
                adapter.setAdapter(data)
            }
        }

    }


    override fun onclick(position: Int) {


        (activity as Home).play(songs,position)


    }


    fun notifyItemChange(position: Int){

        adapter.notifyItemChanged(position)
    }


}