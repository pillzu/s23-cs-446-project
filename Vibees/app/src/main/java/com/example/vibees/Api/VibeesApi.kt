package com.example.vibees.Api

import android.util.Log
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.Models.Playlist
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.Tags
import com.example.vibees.Models.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VibeesApi {

    val apiService = APIInterface()
    var userID by GlobalAppState::UserID
    val profile_url = GlobalAppState.currentUser?.profile_url
    val phone_no = GlobalAppState.currentUser?.phone_no
    val email = GlobalAppState.currentUser?.email


    fun getAllParties(successfn: (List<Party>) -> Unit, failurefn: (Throwable) -> Unit, tags: List<String>) {
        val callResponse = apiService.getAllParties(Tags(tags))
        return callResponse.enqueue(
            object: Callback<List<Party>> {
                override fun onResponse(
                    call: Call<List<Party>>,
                    response: Response<List<Party>>
                ) {
                    successfn(response.body()!!)
                }

                override fun onFailure(call: Call<List<Party>>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }

    fun getParty(successfn: (Party) -> Unit, notfoundfn: () -> Unit, failurefn: (Throwable) -> Unit, party_id: String) {
        val callResponse = apiService.getParty(party_id)
        return callResponse.enqueue(
            object: Callback<Party> {
                override fun onResponse(
                    call: Call<Party>,
                    response: Response<Party>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        notfoundfn()
                    }

                }

                override fun onFailure(call: Call<Party>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }

    fun getPartiesAttending(successfn: (List<Party>) -> Unit, failurefn: (Throwable) -> Unit) {
        var userID by GlobalAppState::UserID
        val callResponseAttending = apiService.getMyPartiesAttending(User(userID!!))
        return callResponseAttending.enqueue(
            object: Callback<List<Party>> {
                override fun onResponse(
                    call: Call<List<Party>>,
                    response: Response<List<Party>>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        failurefn(Throwable(response.errorBody().toString()))
                    }
                }

                override fun onFailure(call: Call<List<Party>>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }

    fun getPartiesHosting(successfn: (List<Party>) -> Unit, failurefn: (Throwable) -> Unit) {
        val callResponseHosting = apiService.getMyPartiesHosting(User(userID!!))
        return callResponseHosting.enqueue(
            object: Callback<List<Party>> {
                override fun onResponse(
                    call: Call<List<Party>>,
                    response: Response<List<Party>>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        failurefn(Throwable(response.errorBody().toString()))
                    }
                }

                override fun onFailure(call: Call<List<Party>>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }

    fun createParty(successfn: (ResponseMessage) -> Unit, failurefn: (ResponseBody) -> Unit, obj: Party) {
        val callResponse = apiService.createParty(obj)
        return callResponse.enqueue(
            object: Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        failurefn(response.errorBody()!!)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Log.d("ERROR", "FAILURE: Create party")
                }
            }
        )
    }

    fun registerUserForParty(successfn: (ResponseMessage) -> Unit, failurefn: (Throwable) -> Unit, party_id: String, songList: List<String>) {
        val callResponse = apiService.attendParty(party_id, Playlist(User(userID!!), songList))
        return callResponse.enqueue(
            object: Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        failurefn(Throwable(response.errorBody().toString()))
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }

    fun registerUser(successfn: (ResponseMessage) -> Unit, failurefn: (Throwable) -> Unit, user: User) {
        val callResponse = apiService.registerUser(user)
        return callResponse.enqueue(
            object: Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        failurefn(Throwable(response.errorBody().toString()))
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }


    fun verifyAttendance(successfn: (Int) -> Unit, failurefn: () -> Unit, party_id: String) {
        val callResponse = apiService.verifyAttendance(party_id, userID.toString())
        return callResponse.enqueue(
            object: Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    successfn(response.code())
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn()
                }
            }
        )
    }


    fun deleteUserAccount(
        user_id: String,
        successfn: (ResponseMessage) -> Unit,
        failurefn: (Throwable) -> Unit
    ) {
        val callResponse = apiService.deleteUserAccount(user_id)
        return callResponse.enqueue(
            object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        failurefn(Throwable(response.errorBody().toString()))
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }


    fun updateUserDetails(
        user_id: String,
        successfn: (ResponseMessage) -> Unit,
        failurefn: (Throwable) -> Unit,
        requestModel: User
    ) {
        val callResponse = apiService.updateUserDetails(user_id, requestModel)
        return callResponse.enqueue(
            object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        failurefn(Throwable(response.errorBody().toString()))
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }

    fun checkQrAttendee(
        user_endpoint: String,
        successfn: (ResponseMessage) -> Unit,
        failurefn: (Throwable) -> Unit,
    ) {
        val callResponse = apiService.checkQrAttendee(user_endpoint)
        return callResponse.enqueue(
            object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.code() == 404) {
                        failurefn(Throwable("Invalid URL!"))
                    } else if (response.code() == 401) {
                        failurefn(Throwable("Invalid Attendee!"))
                    } else {
                        successfn(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }

    fun unattendUserFromParty(
        successfn: (ResponseMessage) -> Unit,
        failurefn: (Throwable) -> Unit,
        party_id: String,
        user_id: String,
    ) {
        val callResponse = apiService.unattendParty(party_id, user_id)
        return callResponse.enqueue(
            object: Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful) {
                        successfn(response.body()!!)
                    } else {
                        failurefn(Throwable(response.errorBody().toString()))
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }
}