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
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    val authContext = this
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted. You can proceed with retrieving the location.
                getLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted. You can proceed with retrieving the location.
                getLocation()
            }
            else -> {
                // No location access granted. Handle the case where permission is denied.
                // You can show a message or take appropriate action.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(ContentValues.TAG, "Auth Started...")
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

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

        val signInUsingGoogle: () -> Unit = {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(ContentValues.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                        Toast.makeText(authContext, "Couldn't start One Tap UI: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(ContentValues.TAG, e.localizedMessage)
                    Toast.makeText(authContext, "${e.localizedMessage}", Toast.LENGTH_LONG).show()
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
                                        val phoneNo = user.phoneNumber


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

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Request the location using the fusedLocationClient, as shown in your previous code.
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Handle the retrieved location, similar to your previous code.
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d(ContentValues.TAG, "LOCATION RETRIEVED")

            }
        }
    }

}