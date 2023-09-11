package com.example.welisten.modules

import com.example.welisten.myLibrary.repository.OfflineSongsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object Module {


    @ViewModelScoped
    @Provides
    fun getRepository(): OfflineSongsRepository {

        return OfflineSongsRepository()
    }


}