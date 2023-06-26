package com.example.vibees.screens.home.myparties

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
import com.example.vibees.GlobalAppState
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType
import com.example.vibees.graphs.PartyScreen

@Composable
fun PartyViewing(
    navController: NavHostController,
    id: String,
) {

    var partyDetails by GlobalAppState::PartyDetails
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
                text = "Party Name",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            horizontalArrangement = Arrangement.Center) {
            Text("Hosted by Hostname", fontWeight = FontWeight.Bold, color = Color.Black)
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
                    Text(color = Color.Black, text = "20 August 2023")
                }
                Column (modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Location",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = "1201-1200 University Ave W")
                    Text(color = Color.Black, text = "Waterloo, Ontario")
                    Text(color = Color.Black, text = "N2L 6C5")
                }
                Column (modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Maximum Capacity",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = "35")
                }
            }

            Column{
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Time",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = "12:30 PM")
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Type",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(color = Color.Black, text = "Board-game")
                    Text(color = Color.Black, text = "Alcohol-free")
                    Text("")
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Entry Fees",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(color = Color.Black, text = "10.0")
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
                Text(color = Color.Black, text = "This is a Board-game party for all Board-game lovers! Join us for a chill night of enjoyment.")
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            horizontalArrangement = Arrangement.Center) {
            androidx.compose.material.Button(
                onClick = { navController.navigate(PartyScreen.Details.passId(id)) },
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