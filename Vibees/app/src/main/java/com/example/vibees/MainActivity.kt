package com.example.vibees

import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.vibees.graphs.RootNavigationGraph
import com.example.vibees.ui.theme.VibeesTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.Manifest
import android.location.Address
import com.example.vibees.Api.VibeesApi
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.User
import com.example.vibees.utils.Geolocation
import com.example.vibees.utils.hashToUUID
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val REQ_ONE_TAP = 2
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private lateinit var geolocation: Geolocation
    private val vibeesApi = VibeesApi()
    private lateinit var sic: CompletableDeferred<Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VibeesTheme {
                RootNavigationGraph(navController = rememberNavController(), signIn = ::signInUsingGoogle)
            }
        }
        initializeAuth()
        initializeSignInRequest()
    }

    private fun initializeAuth() {
        auth = Firebase.auth
    }

    private fun initializeSignInRequest() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private suspend fun signInUsingGoogle(signInComplete: CompletableDeferred<Unit>) {
        sic = signInComplete
        val result = oneTapClient.beginSignIn(signInRequest).await()
        try {
            startIntentSenderForResult(
                result.pendingIntent.intentSender, REQ_ONE_TAP,
                null, 0, 0, 0, null
            )
        } catch (e: IntentSender.SendIntentException) {
            Log.e(ContentValues.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Couldn't start One Tap UI: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
            sic.completeExceptionally(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = googleCredential.googleIdToken
                    if (idToken != null) {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                handleSignInResult(task)
                            }
                    } else {
                        Log.d(ContentValues.TAG, "No ID token!")
                    }
                } catch (e: ApiException) {
                    handleOneTapApiException(e)
                }
            }
        }
    }

    private fun handleOneTapApiException(e: ApiException) {
        when (e.statusCode) {
            CommonStatusCodes.CANCELED -> {
                Log.d(ContentValues.TAG, "One-tap dialog was closed.")
                showToast("One-tap dialog was closed.")
            }
            CommonStatusCodes.NETWORK_ERROR -> {
                Log.d(ContentValues.TAG, "One-tap encountered a network error.")
                showToast("One-tap encountered a network error.")
            }
            else -> {
                Log.d(ContentValues.TAG, "Couldn't get credential from result. (${e.localizedMessage})")
                showToast("Couldn't get credential from result.")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

    private fun handleSignInResult(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val user = auth.currentUser
            val name = user?.displayName
            val parts = name?.split(" ")
            val firstName = parts?.getOrNull(0)
            val lastName = parts?.getOrNull(1)
            val email = user?.email
            val phoneNo = 0
            val profileURL = user?.photoUrl
            val uid = hashToUUID(user!!.uid)
            var latitude = 0.00
            var longitude = 0.00
            var street = ""
            var city = ""
            var province = ""
            var postalCode = ""

            val activityResultRegistry = this@MainActivity.activityResultRegistry
            geolocation = Geolocation(activityResultRegistry)
            val getLoc = geolocation.locationPermissionRequest(this@MainActivity) { lat, long ->
                latitude = lat
                longitude = long
                val address: Address = geolocation.getUserLocation(this@MainActivity, latitude, longitude)
                street = address.thoroughfare
                city = address.locality
                province = address.adminArea
                postalCode = address.postalCode

                val user = User(
                    uid,
                    profileURL,
                    firstName,
                    lastName,
                    phoneNo,
                    street,
                    city,
                    province,
                    postalCode,
                    email
                )

                val successfn: (ResponseMessage) -> Unit = { response ->
                    Log.d("TAG", "$response")
                    GlobalAppState.currentUser = user
                    sic.complete(Unit)
                }

                val failurefn: (Throwable) -> Unit = { t ->
                    Log.d("TAG", "FAILURE")
                    Log.d("TAG", t.printStackTrace().toString())
                }

                val response = vibeesApi.registerUser(successfn, failurefn, user)
            }
            getLoc.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            showToast("Welcome, $name! Sign-in successful.")
        } else {
            Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
            showToast("Sign in failed")
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }
    }
}
