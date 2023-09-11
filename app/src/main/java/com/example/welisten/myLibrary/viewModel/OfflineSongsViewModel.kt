package com.example.welisten.myLibrary.viewModel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.welisten.myLibrary.models.SongModel
import com.example.welisten.myLibrary.repository.OfflineSongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class OfflineSongsViewModel @Inject constructor(private val offlineSongsRepository: OfflineSongsRepository) : ViewModel() {




    @RequiresApi(Build.VERSION_CODES.Q)
    fun getSongs(context: Context) {

        offlineSongsRepository.call(context)


    }


    fun observeSongsLiveData() : MutableLiveData<List<SongModel>> {
        return offlineSongsRepository.get()
    }

}