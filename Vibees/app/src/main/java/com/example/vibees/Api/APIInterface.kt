package com.example.vibees.Api

import com.example.vibees.Models.Party
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

const val url = "http://192.168.0.34:5000"

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

    fun getAllParties(): Call<List<Party>> {
        val tags = Tags()
        return apiService.requestAllParties(tags)
    }

    fun getMyPartiesAttending(requestModel: User): Call<List<Party>> {
        return apiService.requestMyPartiesAttending(requestModel)
    }

    fun getMyPartiesHosting(requestModel: User): Call<List<Party>> {
        return apiService.requestMyPartiesHosting(requestModel)
    }

    fun attendParty(party_id: String, requestModel: User): Call<ResponseMessage> {
        return apiService.registerUserForParty(party_id, requestModel)
    }

    fun registerUser(requestModel: User): Call<ResponseMessage> {
        return apiService.registerOrLoginUser(requestModel)
    }

    fun getPartyInfo(requestModel: Transaction): Call<PaymentMetaData> {
        return apiService.getPaymentInfo(requestModel)
    }
}