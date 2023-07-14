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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import com.example.vibees.Api.VibeesApi
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.User
import com.example.vibees.utils.Geolocation
import com.example.vibees.utils.hashToUUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID

class MainActivity : ComponentActivity() {
    val authContext = this
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private lateinit var geolocation: Geolocation
    val vibeesApi = VibeesApi()
    private lateinit var sic: CompletableDeferred<Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(ContentValues.TAG, "Auth Started...")
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        val signInUsingGoogle: suspend (signInComplete: CompletableDeferred<Unit>) -> Unit = { signInComplete ->
            sic = signInComplete
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(ContentValues.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                        Toast.makeText(authContext, "Couldn't start One Tap UI: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        sic?.completeExceptionally(e) // Complete exceptionally if there's an error
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(ContentValues.TAG, e.localizedMessage)
                    Toast.makeText(authContext, "${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    sic?.completeExceptionally(e) // Complete exceptionally if there's an error
                }
        }

        setContent {
            VibeesTheme {
                RootNavigationGraph(navController = rememberNavController(), signIn = signInUsingGoogle)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = googleCredential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                                        val user = auth.currentUser
                                        // updateUI(user)
                                        Log.d(ContentValues.TAG, user?.email!!)
                                        val name = user.displayName
                                        val parts = name?.split(" ")
                                        val firstName = parts?.getOrNull(0)
                                        val lastName = parts?.getOrNull(1)
                                        val email = user.email
                                        val phoneNo = 0
                                        Log.d(ContentValues.TAG, "${user.phoneNumber}")
                                        val profileURL = user.photoUrl
                                        val uid = hashToUUID(user.uid)
                                        var latitude = 0.00
                                        var longitude = 0.00
                                        var street = ""
                                        var city = ""
                                        var province = ""
                                        var postalCode = ""
                                        // retrieve user location
                                        val activityResultRegistry = this@MainActivity.activityResultRegistry
                                        geolocation = Geolocation(activityResultRegistry)
                                        val getLoc = geolocation.locationPermissionRequest(this) { lat, long ->
                                            // Handle the latitude and longitude values here in MainActivity
                                            latitude = lat
                                            longitude = long
                                            val address: Address = geolocation.getUserLocation(this, latitude, longitude)
                                            street = address.thoroughfare
                                            city = address.locality
                                            province = address.adminArea
                                            postalCode = address.postalCode
                                            val user = User(uid, profileURL, firstName, lastName, phoneNo, street, city, province, postalCode, email)
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

                                        Toast.makeText(authContext, "Welcome, $name! Sign-in successful.", Toast.LENGTH_LONG).show()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                                        // updateUI(null)
                                        Log.d(ContentValues.TAG, "Sign in failed...")
                                        Toast.makeText(authContext, "Sign in failed", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(ContentValues.TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(ContentValues.TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                            Toast.makeText(authContext, "One-tap dialog was closed.", Toast.LENGTH_LONG).show()
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(ContentValues.TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                            Toast.makeText(authContext, "One-tap encountered a network error.", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Log.d(
                                ContentValues.TAG, "Couldn't get credential from result." +
                                        " (${e.localizedMessage})")
                            Toast.makeText(authContext, "Couldn't get credential from result.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        // updateUI(currentUser)
//        Log.d(ContentValues.TAG, currentUser?.phoneNumber!!)
    }

}