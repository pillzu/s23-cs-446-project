package com.example.vibees.screens.home.myparties

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.example.vibees.Api.APIInterface
import com.example.vibees.Api.VibeesApi
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.User
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType
import com.example.vibees.graphs.PartyScreen
import com.example.vibees.screens.bottombar.BottomBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun PartyViewing(
    navController: NavHostController,
    id: String,
) {

    var userID by GlobalAppState::UserID
    val apiService = APIInterface()
    var partyDetails by GlobalAppState::PartyDetails
    val vibeesApi = VibeesApi()

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
                    Text(color = Color.Black, text = partyDetails?.date_time!!)
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
}