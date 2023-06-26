package com.example.vibees.Api

import android.content.Context
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.User
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIInterface {
    private val apiService: ApiService

    init {
        val url = "http://192.168.0.179:5000"

        val gson = GsonBuilder()
            .setDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun createParty(requestModel: Party): Call<ResponseMessage> {
        return apiService.requestParty(requestModel)
    }

    fun getAllParties(): Call<List<Party>> {
        return apiService.requestAllParties()
    }

    fun getMyPartiesAttending(requestModel: User): Call<List<Party>> {
        return apiService.requestMyPartiesAttending(requestModel)
    }

    fun getMyPartiesHosting(requestModel: User): Call<List<Party>> {
        return apiService.requestMyPartiesHosting(requestModel)
    }
}