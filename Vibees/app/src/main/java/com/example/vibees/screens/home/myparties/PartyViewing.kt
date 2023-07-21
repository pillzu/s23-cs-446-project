package com.example.vibees.screens.home.myparties

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.example.vibees.Api.APIInterface
import com.example.vibees.Api.LaunchBackgroundEffect
import com.example.vibees.Api.VibeesApi
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.screens.bottombar.BottomBar
import java.time.format.DateTimeFormatter

@Composable
fun PartyViewing(
    navController: NavHostController,
    id: String,
) {

    var userID by GlobalAppState::UserID
    val apiService = APIInterface()
    var partyDetails by GlobalAppState::PartyDetails
    val vibeesApi = VibeesApi()

    var idfound by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    // Successful request
    val successfn: (ResponseMessage) -> Unit = { response ->
        Log.d("TAG", "${response.message}")
        navController.navigate(BottomBar.MyParties.route)
    }

    // failed request
    val failurefn: (Throwable) -> Unit = { t ->
        Log.d("TAG", "FAILURE")
        Log.d("TAG", t.printStackTrace().toString())
    }

    // get success and failure functions for getting data for id
    // Successful request for id
    val idsuccessfn: (ResponseMessage) -> Unit = { response ->
        Log.d("TAG", "${response.message}")
        loading = false
        idfound = true
    }

    // failed request for id
    val idfailurefn: (Throwable) -> Unit = { t ->
        Log.d("TAG", "FAILURE")
        loading = false
        idfound = false
    }

    LaunchBackgroundEffect(key = Unit) {
        Log.d("PARTY_ID", id)
        //val response = vibeesApi.getParty(idsuccessfn, idfailurefn, id)

        // also add share button to generate link
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
        if (idfound) {
            // show party details
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    horizontalArrangement = Arrangement.Center) {
                    androidx.compose.material.Button(
                        onClick = {
                            val callResponse = vibeesApi.registerUserForParty(successfn, failurefn, partyDetails?.party_id!!)
                        },
                        modifier = Modifier.padding(20.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
                    ) {
                        androidx.compose.material.Text(
                            text = "Attend Party",
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
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }


}