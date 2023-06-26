package com.example.vibees.Api

import android.content.Context
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService  {

    @POST("/parties/host")
    fun requestParty(@Body requestModel: Party): Call<ResponseMessage>

    @POST("/parties")
    fun requestAllParties(): Call<List<Party>>

    @POST("/user/parties/attend")
    fun requestMyPartiesAttending(@Body requestModel: User): Call<List<Party>>

    @POST("/user/parties/host")
    fun requestMyPartiesHosting(@Body requestModel: User): Call<List<Party>>
}