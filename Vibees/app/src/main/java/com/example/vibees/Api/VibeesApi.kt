package com.example.vibees.Api

import android.util.Log
import android.widget.Toast
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.User
import com.example.vibees.screens.bottombar.BottomBar
import com.example.vibees.utils.hashToUUID
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VibeesApi {

    val apiService = APIInterface()
    var userID by GlobalAppState::UserID
    val profile_url = GlobalAppState.currentUser?.profile_url
    val phone_no = GlobalAppState.currentUser?.phone_no
    val email = GlobalAppState.currentUser?.email


    fun getAllParties(successfn: (List<Party>) -> Unit, failurefn: (Throwable) -> Unit) {
        val callResponse = apiService.getAllParties()
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

    fun getPartiesAttending(successfn: (List<Party>) -> Unit, failurefn: (Throwable) -> Unit) {
        var userID by GlobalAppState::UserID
        val callResponseAttending = apiService.getMyPartiesAttending(User(userID!!))
        return callResponseAttending.enqueue(
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

    fun getPartiesHosting(successfn: (List<Party>) -> Unit, failurefn: (Throwable) -> Unit) {
        val callResponseHosting = apiService.getMyPartiesHosting(User(userID!!))
        return callResponseHosting.enqueue(
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

    fun createParty(successfn: (ResponseMessage) -> Unit, failurefn: (Throwable) -> Unit, obj: Party) {
        val callResponse = apiService.createParty(obj)
        return callResponse.enqueue(
            object: Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    successfn(response.body()!!)
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }

    fun registerUserForParty(successfn: (ResponseMessage) -> Unit, failurefn: (Throwable) -> Unit, party_id: String) {
        val callResponse = apiService.attendParty(party_id, User(userID!!))
        return callResponse.enqueue(
            object: Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    successfn(response.body()!!)
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
                    successfn(response.body()!!)
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
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
                    successfn(response.body()!!)
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
                    successfn(response.body()!!)
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    failurefn(t)
                }
            }
        )
    }


}