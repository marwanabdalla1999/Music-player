package com.example.welisten.network

import com.example.welisten.const.Credentials
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {



    fun getInstance(): Retrofit {

        return Retrofit.Builder().baseUrl(Credentials.Url)

            .addConverterFactory(GsonConverterFactory.create())


            .build()
    }

}