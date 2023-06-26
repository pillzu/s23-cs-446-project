package com.example.vibees.Api

import android.content.Context
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService  {

    @POST("/parties/host")
    fun requestParty(@Body requestModel: Party): Call<ResponseMessage>

    @POST("/parties")
    fun requestAllParties(): Call<List<Party>>
}