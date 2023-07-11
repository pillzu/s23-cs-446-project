package com.example.vibees.Api

import android.content.Context
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.User
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val url = "http://172.22.11.70:5000"

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
        return apiService.requestAllParties()
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
}