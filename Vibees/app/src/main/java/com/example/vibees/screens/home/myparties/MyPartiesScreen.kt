package com.example.vibees.screens.home.myparties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun MyPartiesScreen() {
    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        // test data
        val parties = listOf(
            Party(
                title = "Party 1",
                date = "1 January 2023",
                time = "23:00",
                host = "Eren",
                location = "123 Waterloo",
                icon = Icons.Default.Add
            ),
            Party(
                title = "Party 2",
                date = "2 January 2023",
                time = "23:00",
                host = "Armin",
                location = "123 Waterloo",
                icon = Icons.Default.Add
            ),
            Party(
                title = "Party 3",
                date = "3 January 2023",
                time = "23:00",
                host = "Mikasa",
                location = "123 Waterloo",
                icon = Icons.Default.Add
            ),
            Party(
                title = "Party 4",
                date = "4 January 2023",
                time = "23:00",
                host = "Erwin",
                location = "123 Waterloo",
                icon = Icons.Default.Add
            ),
            Party(
                title = "Party 5",
                date = "5 January 2023",
                time = "23:00",
                host = "Levi",
                location = "123 Waterloo",
                icon = Icons.Default.Add
            ),
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column{
                Text(
                    text = "MY",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "PARTIES",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displayLarge
                )
            }
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(100.dp)
            )
        }

        // search bar
        

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
        ) {
            item {
                Text(
                    text = "Hosted by Me",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                )
            }
            items(parties.size) {
                PartyItem(partyinfo = parties[it])
            }
            item {
                Text(
                    text = "Upcoming Parties",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(15.dp)
                )
            }
            items(parties.size) {
                PartyItem(partyinfo = parties[it])
            }
        }
    }
}