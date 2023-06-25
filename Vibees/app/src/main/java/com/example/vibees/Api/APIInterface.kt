package com.example.vibees.Api

import android.content.Context
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIInterface {
    private val apiService: ApiService

    init {
        val url = "http://localhost:5000"

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun createParty(requestModel: Party): Call<ResponseMessage> {
        return apiService.requestParty(requestModel)
    }
}