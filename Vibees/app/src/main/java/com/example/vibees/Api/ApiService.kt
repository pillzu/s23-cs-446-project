package com.example.vibees.Api

import com.example.vibees.Models.Party
import com.example.vibees.Models.Playlist
import com.example.vibees.Models.PaymentMetaData
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.Tags
import com.example.vibees.Models.Transaction
import com.example.vibees.Models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ApiService {

    @POST("/parties/host")
    fun requestParty(@Body requestModel: Party): Call<ResponseMessage>

    @GET("/parties/{party_id}")
    fun requestGeneralParty(@Path("party_id") party_id: String): Call<Party>

    @POST("/parties")
    fun requestAllParties(@Body requestModel: Tags): Call<List<Party>>

    @POST("/user/parties/attend")
    fun requestMyPartiesAttending(@Body requestModel: User): Call<List<Party>>

    @POST("/user/parties/host")
    fun requestMyPartiesHosting(@Body requestModel: User): Call<List<Party>>

    @POST("/parties/attend/{party_id}")
    fun registerUserForParty(
        @Path("party_id") party_id: String,
        @Body requestModel: Playlist
    ): Call<ResponseMessage>

    @POST("/user")
    fun registerOrLoginUser(@Body requestModel: User): Call<ResponseMessage>


    @GET("/party/qr/{party_id}/{guest_id}")
    fun verifyAttendance(@Path("party_id") party_id: String, @Path("guest_id") guest_id:String): Call<ResponseMessage>

    @PUT("/user/{user_id}")
    fun updateUserDetails(@Path("user_id") user_id: String, @Body requestModel: User): Call<ResponseMessage>

    @DELETE("/user/{user_id}")
    fun deleteUserAccount(@Path("user_id") user_id: String): Call<ResponseMessage>

    @GET("{qr_url}")
    fun checkQrAttendee(@Path("qr_url", encoded = true) qr_endpoint: String): Call<ResponseMessage>


    @POST("/payment-sheet")
    fun getPaymentInfo(@Body requestModel: Transaction): Call<PaymentMetaData>

    @DELETE("/parties/unattend/{party_id}/{user_id}")
    fun unattendUserFromParty(
        @Path("party_id") party_id: String,
        @Path("user_id") user_id: String
    ): Call<ResponseMessage>

    @DELETE("/parties/cancel/{party_id}")
    fun cancelParty(
        @Path("party_id") party_id: String
    ): Call<ResponseMessage>

    @PUT("/party/update/{party_id}")
    fun updatePartyDetails(
        @Path("party_id") party_id: String,
        @Body requestModel: Party
    ): Call<ResponseMessage>
}