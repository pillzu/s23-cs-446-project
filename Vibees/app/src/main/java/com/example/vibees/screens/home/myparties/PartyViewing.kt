package com.example.vibees.screens.home.myparties

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.example.vibees.Api.APIInterface
import com.example.vibees.Api.LaunchBackgroundEffect
import com.example.vibees.Api.VibeesApi
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.User
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType
import com.example.vibees.graphs.PartyScreen
import com.example.vibees.payment.CheckoutActivity
import com.example.vibees.screens.bottombar.BottomBar
import java.time.format.DateTimeFormatter

@Composable
fun PartyViewing(
    navController: NavHostController,
    id: String,
) {

    var userID by GlobalAppState::UserID
    var UserName by GlobalAppState::UserName
    val apiService = APIInterface()
    var partyDetails by GlobalAppState::PartyDetails
    val vibeesApi = VibeesApi()
    val activity = LocalContext.current as? ComponentActivity
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result here if needed
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle successful result
        } else {
            // Handle other result codes or errors
        }
    }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var idfound by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    var attends by remember {
        mutableStateOf(false)
    }

    // verify the user attends the party
    val attendfn: (Int) -> Unit = { code ->
        attends = code == 200
    }

    // verify the user does not attend the part
    val unattendfn: () -> Unit = {
        attends = false
    }

    // get success and failure functions for getting data for id
    // Successful request for id
    val idsuccessfn: (Party) -> Unit = { response ->
        loading = false
        idfound = true
        partyDetails = response
    }

    // failed request for id
    val idfailurefn: () -> Unit = {
        Log.d("TAG", "FAILURE: could not find id")
        loading = false
        idfound = false
    }

    val serverfailurefn: (Throwable) -> Unit = { t ->
        Log.d("TAG", "FAILURE: server error")
        Log.d("TAG", t.printStackTrace().toString())
        loading = false
        idfound = false
    }

    if (partyDetails == null) {
        LaunchBackgroundEffect(key = Unit) {
            Log.d("PARTY_ID", id)
            loading = true
            val response = vibeesApi.getParty(idsuccessfn, idfailurefn, serverfailurefn, id)
        }
    }


    if (loading) {
        // show loading screen
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else {
        // check if id found or not
        if (idfound or (partyDetails != null)) {
            val att = vibeesApi.verifyAttendance(attendfn, unattendfn, id)
            idfound = true
            // show party details
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp)
                            .size(40.dp)
                            .clickable {
                                clipboardManager.setText(AnnotatedString("https://vibees.ca/party/${id}"))
                                Toast
                                    .makeText(
                                        context,
                                        "Invite link copied to clipboard!",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                    )
                }

                Row(horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Party Icon",
                        modifier = Modifier
                            .size(100.dp)
                    )
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    horizontalArrangement = Arrangement.Center) {
                    androidx.compose.material3.Text(
                        text = partyDetails?.name!!,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    horizontalArrangement = Arrangement.Center) {
                    Text("Hosted by ${partyDetails?.host_name}", fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)) {
                    Column{
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Date",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(color = Color.Black, text = partyDetails?.date_time!!.format(
                                DateTimeFormatter.ISO_DATE)
                            )
                        }
                        Column (modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Location",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(color = Color.Black, text = partyDetails?.street!!)
                            Text(color = Color.Black, text = "${partyDetails?.city}, ${partyDetails?.prov}")
                            Text(color = Color.Black, text = partyDetails?.postal_code!!)
                        }
                        Column (modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Maximum Capacity",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(color = Color.Black, text = partyDetails?.max_cap.toString())
                        }
                    }

                    Column{
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Type",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                            Text(color = Color.Black, text = "EDM")
                            Text(color = Color.Black, text = "Alcohol-free")
                            Text("")
                        }
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Entry Fees",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                            Text(color = Color.Black, text = partyDetails?.entry_fee.toString())
                        }
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Description",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(color = Color.Black, text = partyDetails?.desc!!)
                    }
                }

                var songString by remember { mutableStateOf("") }
                if (!attends) {
                    Column(modifier = Modifier.fillMaxWidth(),
                           horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Attending this party? Give us your song recommendations!",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            modifier = Modifier.fillMaxWidth().padding(12.dp, 5.dp, 12.dp, 5.dp),
                            color = Color.Black)
                        Text(text = "Enter song names separated by a comma (minimum 1)",
                            fontWeight = FontWeight.Light,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            modifier = Modifier.fillMaxWidth().padding(12.dp, 0.dp, 12.dp, 5.dp),
                            color = Color.Black)
                        OutlinedTextField(modifier = Modifier.fillMaxWidth().padding(12.dp, 0.dp, 12.dp, 0.dp),
                            value = songString,
                            onValueChange = {
                                songString = it
                            },
                            placeholder = {
                                androidx.compose.material3.Text(
                                    "eg: Calm Down, Dynamite",
                                    color = Color.Gray
                                )
                            }
                        )
                    }
                }
                val songList = mutableListOf(*songString.split(",").toTypedArray())
                var canAttend by remember { mutableStateOf(false) }
                if (songString != "") { canAttend = true }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    horizontalArrangement = Arrangement.Center) {
                    androidx.compose.material.Button(
                        onClick = {
                            val intent = Intent(activity, CheckoutActivity::class.java)
                            intent.putExtra("entryFee", partyDetails?.entry_fee)
                            val userId = userID.toString()
                            intent.putExtra("userID", userId)
                            val userName: String = UserName!!
                            intent.putExtra("userName", userName)
                            intent.putExtra("partyID", partyDetails?.party_id!!)
                            launcher.launch(intent)
                        },
                        modifier = Modifier.padding(20.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = Color.Gray),
                        enabled = (!attends && canAttend)
                    ) {
                        androidx.compose.material.Text(
                            text = if (attends) "Attended" else "Attend Party",
                            color = Color.Black
                        )
                    }
                }
            }
        } else {
            // show not found screen
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon",
                    modifier = Modifier
                        .size(500.dp)
                        .padding(20.dp)
                )
                Text(
                    text = "Could not retrieve the party details. Selected party or invite is invalid :(",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    modifier = Modifier.padding(8.dp),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    BackHandler {
        navigatingBack(navController, idfound)
    }

}

fun navigatingBack(
    navController: NavHostController,
    idfound: Boolean
) {
    if (!idfound) {
        // go back to home instead of details screen
        navController.popBackStack()
        navController.popBackStack()
    } else {
        navController.popBackStack()
    }
}



//fun navigatingBack(
//    navController: NavHostController,
//    destinationRoute: String
//) {
//    val hasBackstackTheDestinationRoute = navController.currentBackStack.value.find {
//        it.destination.route == destinationRoute
//    } != null
//    // if the destination is already in the backstack, simply go back
//    if (hasBackstackTheDestinationRoute) {
//        navController.popBackStack()
//    } else {
//        // otherwise, navigate to a new destination popping the current destination
//        navController.navigate(destinationRoute) {
//            navController.currentBackStackEntry?.destination?.route?.let {
//                popUpTo(it) {
//                    inclusive = true
//                }
//            }
//        }
//    }
//}