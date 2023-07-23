package com.example.vibees.screens.home.myparties

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.example.vibees.Api.APIInterface
import com.example.vibees.GlobalAppState
import com.example.vibees.graphs.HostScreens
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType
import java.time.format.DateTimeFormatter

var URL = "https://www.youtube.com/watch?v=xvFZjo5PgG0&ab_channel=Duran"

@Composable
fun PartyDetails(
    navController: NavHostController,
    id: String,
) {
    var userID by GlobalAppState::UserID
    val apiService = APIInterface()
    var partyDetails by GlobalAppState::PartyDetails
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
                    .size(40.dp)
                    .clickable {
                        clipboardManager.setText(AnnotatedString("https://vibees.ca/party/${id}"))
                        Toast.makeText(context, "Invite link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
            )
            if (partyDetails?.user_id == userID) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                        .size(40.dp)
                        .clickable {
                            navController.navigate(HostScreens.Step1.route)
                        }
                )
            }

        }
        // QR Code section
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(top = 50.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.Yellow)
                    .clip(RoundedCornerShape(15.dp))
            ) {
                Barcode(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 20.dp, vertical = 15.dp)
                        .width(250.dp)
                        .height(250.dp),
                    resolutionFactor = 10, // Optionally, increase the resolution of the generated image
                    type = BarcodeType.QR_CODE, // pick the type of barcode you want to render
                    value = "${URL}${partyDetails?.qr_endpoint}" // The textual representation of this code
                )
                Text(
                    text = partyDetails?.name!!,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(15.dp)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Party Icon",
                modifier = Modifier
                    .size(100.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = partyDetails?.name!!,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Hosted by ${partyDetails?.host_name}",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Date",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        color = Color.Black, text = partyDetails?.date_time!!.format(
                            DateTimeFormatter.ISO_DATE
                        )
                    )
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Location",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = partyDetails?.street!!)
                    Text(
                        color = Color.Black,
                        text = "${partyDetails?.city}, ${partyDetails?.prov}"
                    )
                    Text(color = Color.Black, text = partyDetails?.postal_code!!)
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Maximum Capacity",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = partyDetails?.max_cap.toString())
                }
            }

            Column {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Description",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(color = Color.Black, text = partyDetails?.desc!!)
            }
        }
    }
}