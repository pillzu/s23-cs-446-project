package com.example.vibees.Api

import com.example.vibees.Models.Party
import com.example.vibees.Models.Playlist
import com.example.vibees.Models.PaymentMetaData
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.Tags
import com.example.vibees.Models.Transaction
import com.example.vibees.Models.User
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val url = "http://192.168.183.85:5000"

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        val dateString = json?.asString
        val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
        return LocalDateTime.parse(dateString, formatter)
    }
}

class APIInterface {
    private val apiService: ApiService

    init {

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
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

    fun getParty(party_id: String): Call<Party> {
        return apiService.requestGeneralParty(party_id)
    }

    fun getAllParties(tags: Tags): Call<List<Party>> {
        return apiService.requestAllParties(tags)
    }

    fun getMyPartiesAttending(requestModel: User): Call<List<Party>> {
        return apiService.requestMyPartiesAttending(requestModel)
    }

    fun getMyPartiesHosting(requestModel: User): Call<List<Party>> {
        return apiService.requestMyPartiesHosting(requestModel)
    }

    fun attendParty(party_id: String, requestModel: Playlist): Call<ResponseMessage> {
        return apiService.registerUserForParty(party_id, requestModel)
    }

    fun registerUser(requestModel: User): Call<ResponseMessage> {
        return apiService.registerOrLoginUser(requestModel)
    }

    fun verifyAttendance(party_id: String, guest_id: String): Call<ResponseMessage> {
        return apiService.verifyAttendance(party_id, guest_id)
    }

    fun deleteUserAccount(user_id: String): Call<ResponseMessage> {
        return apiService.deleteUserAccount(user_id)
    }

    fun updateUserDetails(user_id: String, user_model: User): Call<ResponseMessage> {
        return apiService.updateUserDetails(user_id, user_model)
    }

    fun checkQrAttendee(user_qr: String): Call<ResponseMessage> {
        return apiService.checkQrAttendee(qr_endpoint = user_qr )
    }

    fun getPartyInfo(requestModel: Transaction): Call<PaymentMetaData> {
        return apiService.getPaymentInfo(requestModel)
    }
}