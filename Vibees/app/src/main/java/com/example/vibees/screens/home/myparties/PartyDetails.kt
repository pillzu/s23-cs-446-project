package com.example.vibees.screens.home.myparties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

@Composable
fun PartyDetails(
    navController: NavHostController,
    id: String,
) {
    Column(
    ) {
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
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(20.dp)
                )
                Text(
                    text = id,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column{
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Date",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = "20 August 2023")
                }
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Location",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = "1200 University Ave Waterloo")
                }

            }

            Column{
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Time",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = "12:30 PM")
                }
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Host",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(color = Color.Black, text = "Samuel Smith")
                }
            }
        }
    }
}