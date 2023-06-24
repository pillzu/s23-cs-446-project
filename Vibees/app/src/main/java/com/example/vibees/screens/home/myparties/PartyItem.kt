package com.example.vibees.screens.home.myparties

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PartyItem(
    partyinfo: Party,
    onClick: (id: String) -> Unit
) {
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(1.dp, Color.Black,RoundedCornerShape(20.dp))
            .padding(5.dp)
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .clickable { onClick("test_id") }
            ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Column {
                Text(
                    text = partyinfo.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text(
                        text = "Time: ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = partyinfo.time,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Row {
                    Text(
                        text = "Host: ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = partyinfo.host,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Row {
                    Text(
                        text = "Location: ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = partyinfo.location,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = partyinfo.date,
                    style = MaterialTheme.typography.bodyLarge
                )
                Image(
                    imageVector = partyinfo.icon,
                    contentDescription = "Party icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.Yellow)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "QR Code icon"
                )
                Text(
                    text = "SCAN QR",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.Red)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Cancel icon"
                )
                Text(
                    text = "CANCEL PARTY",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}